#!groovy

// Configuring builder:
// Manage Jenkins -> Global Tool Configuration -> Maven installations -> Add Maven:
// Name - M3
// MAVEN_HOME - path to Maven3 home dir
//
// Manage Jenkins -> Configure System -> Environment variables
// SS_TEST_NODE_CORE16 - ip of SS node for smoke tests 
//
// Approve methods:
// in build job log you will see 
// Scripts not permitted to use new <method>
// Goto http://jenkins.domain/scriptApproval/
// and approve methods denied methods
//
// TODO:
// https://jenkins.io/doc/pipeline/steps/ssh-agent/#sshagent-ssh-agent
notifyBuildDetails = ""
hubIp = ""
cdnHost = ""

node() {
    // Send job started notifications
    try {
        notifyBuild('STARTED')

        def mvnHome = tool 'M3'
        def workspace = pwd()

        stage("Build management deb/template")
        // Use maven to to build deb and template files of management
        notifyBuildDetails = "\nFailed Step - Build management deb/template"

        checkout scm
        def artifactVersion = getVersion("management/pom.xml")
        String debFileName = "management-${env.BRANCH_NAME}.deb"
        String templateFileName = "management-subutai-template_${artifactVersion}-${env.BRANCH_NAME}_amd64.tar.gz"

        commitId = sh(script: "git rev-parse HEAD", returnStdout: true)
        String serenityReportDir = "/var/lib/jenkins/www/serenity/${commitId}"

        // declare hub address
        switch (env.BRANCH_NAME) {
            case ~/master/: hubIp = "masterbazaar.subutai.io"; break;
            case ~/dev/: hubIp = "devbazaar.subutai.io"; break;
            case ~/sysnet/: hubIp = "devbazaar.subutai.io"; break;
            default: hubIp = "bazaar.subutai.io"
        }

        switch (env.BRANCH_NAME) {
            case ~/master/: cdnHost = "mastercdn.subutai.io"; break;
            case ~/dev/: cdnHost = "devcdn.subutai.io"; break;
            case ~/sysnet/: cdnHost = "sysnetcdn.subutai.io"; break;
            default: cdnHost = "cdn.subutai.io"
        }

        // build deb
        sh """
		cd management
		export GIT_BRANCH=${env.BRANCH_NAME}
		sed 's/export HUB_IP=.*/export HUB_IP=${hubIp}/g' -i server/server-karaf/src/main/assembly/bin/setenv
		sed 's/export CDN_IP=.*/export CDN_IP=${cdnHost}/g' -i server/server-karaf/src/main/assembly/bin/setenv

		if [[ "${env.BRANCH_NAME}" == "dev" ]] || [[ "${env.BRANCH_NAME}" == "hotfix-"* ]]; then
			${mvnHome}/bin/mvn clean install -P deb -Dgit.branch=${env.BRANCH_NAME}
		else 
			${mvnHome}/bin/mvn clean install -Dmaven.test.skip=true -P deb -Dgit.branch=${env.BRANCH_NAME}
		fi		
		find ${workspace}/management/server/server-karaf/target/ -name *.deb | xargs -I {} mv {} ${workspace}/${
            debFileName
        }
	    """
        // Start MNG-RH Lock
        lock('debian_slave_node') {
            // create management template
            sh """
			set +x
			ssh admin@${env.debian_slave_node} <<- EOF
			set -e
			
			sudo subutai destroy management
			sudo subutai import debian-stretch
			sudo subutai clone debian-stretch management
			/bin/sleep 20
			scp ubuntu@${env.master_node}:/mnt/lib/lxc/jenkins/${workspace}/${debFileName} /var/snap/subutai-dev/common/lxc/management/rootfs/tmp/
			sudo subutai attach management "apt-get update && apt-get install dirmngr -y"
            sudo subutai attach management "gpg --keyserver pgp.mit.edu --recv 80260C65A4D79BC8"
			sudo subutai attach management "gpg --export --armor 80260C65A4D79BC8 | apt-key add"
			sudo subutai attach management "echo 'deb http://${cdnHost}:8080/kurjun/rest/apt /' > /etc/apt/sources.list.d/subutai-repo.list"
            sudo subutai attach management "apt-get update"
			sudo subutai attach management "sync"
			sudo subutai attach management "apt-get -y install curl influxdb influxdb-certs openjdk-8-jre"
			sudo subutai attach management "wget -q 'https://${cdnHost}:8338/kurjun/rest/raw/get?owner=subutai&name=influxdb.conf' -O /etc/influxdb/influxdb.conf"
			sudo subutai attach management "dpkg -i /tmp/${debFileName}"
			sudo subutai attach management "systemctl stop management"
			sudo subutai attach management "rm -rf /opt/subutai-mng/keystores/"
			sudo subutai attach management "apt-get clean"
			sudo subutai attach management "sync"
            sudo subutai attach management "sed -i "s/weekly/dayly/g" /etc/logrotate.d/rsyslog"
            sudo subutai attach management "sed -i "/delaycompress/d" /etc/logrotate.d/rsyslog"
            sudo subutai attach management "sed -i "s/7/3/g" /etc/logrotate.d/rsyslog"
            sudo subutai attach management "sed -i "s/4/3/g" /etc/logrotate.d/rsyslog"
  			sudo rm /var/snap/subutai-dev/common/lxc/management/rootfs/tmp/${debFileName}
			sudo subutai export management -v ${artifactVersion}-${env.BRANCH_NAME}

			EOF"""
        }
        // upload template to jenkins master node
        sh """
        set +x
        scp admin@${env.debian_slave_node}:/var/snap/subutai-dev/common/lxc/tmpdir/management-subutai-template_${artifactVersion}-${env.BRANCH_NAME}_amd64.tar.gz ${workspace}
        """
        /* stash p2p binary to use it in next node() */
        stash includes: "management-*.deb", name: 'deb'
        stash includes: "management-subutai-template*", name: 'template'

        stage("Update management on test node")
        // Deploy built template to remore test-server
        notifyBuildDetails = "\nFailed on Stage - Update management on test node"

        // Start Test-Peer Lock
        if (env.BRANCH_NAME == 'dev' || env.BRANCH_NAME ==~ /hotfix-.*/ || env.BRANCH_NAME == 'jenkinsfile') {
            lock('debian_slave_node') {
                // destroy existing management template on test node and install latest available snap
                sh """
				set +x
				ssh admin@${env.debian_slave_node} <<- EOF
				set -e
				sudo subutai-dev destroy everything
				if test -f /var/snap/subutai-dev/current/p2p.save; then sudo rm /var/snap/subutai-dev/current/p2p.save; fi
				cd /tmp
				sudo find /tmp -maxdepth 1 -type f -name 'subutai-dev_*' -delete
				sudo snap download subutai-dev --beta
				sudo snap install --dangerous --devmode /tmp/subutai-dev_*.snap
			EOF"""

                // copy generated management template on test node
            /*    sh """
				set +x
				scp ${workspace}/management-subutai-template_${artifactVersion}-${env.BRANCH_NAME}_amd64.tar.gz admin@${env.debian_slave_node}:/var/snap/subutai-dev/common/lxc/tmpdir
			""" */

                // install generated management template
                sh """
				set +x
				ssh admin@${env.debian_slave_node} <<- EOF
				set -e
				sudo sed 's/branch = .*/branch = ${env.BRANCH_NAME}/g' -i /var/snap/subutai-dev/current/agent.gcfg
				sudo sed 's/URL =.*/URL = devcdn.subutai.io/g' -i /var/snap/subutai-dev/current/agent.gcfg
				sudo subutai-dev import management --local
			EOF"""

                /* wait until SS starts */
                timeout(time: 5, unit: 'MINUTES') {
                    sh """
					set +x
					echo "Waiting SS"
					while [ \$(curl -k -s -o /dev/null -w %{http_code} 'https://${env.debian_slave_node}:8443/rest/v1/peer/ready') != "200" ]; do
						sleep 5
					done
				"""
                }

                stage("Integration tests")
                deleteDir()

                // Run Serenity Tests
                notifyBuildDetails = "\nFailed on Stage - Integration tests\nSerenity Tests Results:\n${env.JENKINS_URL}serenity/${commitId}"

                git url: "https://github.com/subutai-io/playbooks.git"
                sh """
				set +e
				./run_tests_qa.sh -m ${env.debian_slave_node}
				./run_tests_qa.sh -s all
				${mvnHome}/bin/mvn integration-test -Dwebdriver.firefox.profile=src/test/resources/profilePgpFF
				OUT=\$?
				${mvnHome}/bin/mvn serenity:aggregate
				cp -rl target/site/serenity ${serenityReportDir}
				if [ \$OUT -ne 0 ];then
					exit 1
				fi
			"""
            }
        }

        if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev' || env.BRANCH_NAME == 'sysnet') {
            stage("Deploy artifacts on kurjun")
            deleteDir()

            unstash 'deb'
            unstash 'template'

            // Deploy built and tested artifacts to cdn
            notifyBuildDetails = "\nFailed on Stage - Deploy artifacts on kurjun"

            // cdn auth creadentials
            String user = "jenkins"
            def authID = sh(script: """
			set +x
			curl -s -k https://${cdnHost}:8338/kurjun/rest/auth/token?user=${user} | gpg --clearsign --no-tty
			""", returnStdout: true)
            def token = sh(script: """
			set +x
			curl -s -k -Fmessage=\"${authID}\" -Fuser=${user} https://${cdnHost}:8338/kurjun/rest/auth/token
			""", returnStdout: true)

            // upload artifacts on cdn
            // upload deb
            String responseDeb = sh(script: """
			set +x
			curl -s -k https://${cdnHost}:8338/kurjun/rest/apt/info?name=${debFileName}
			""", returnStdout: true)
            sh """
			set +x
			curl -s -k -Ffile=@${debFileName} -Ftoken=${token} -H "token: ${token}" https://${cdnHost}:8338/kurjun/rest/apt/upload
            """
            sh """
			set +x
            curl -k -H "token: ${token}" "https://${cdnHost}:8338/kurjun/rest/apt/generate" 
		    """
            
            // def signatureDeb = sh (script: "curl -s -k -Ffile=@${workspace}/${debFileName} -Ftoken=${token} https://${cdnHost}:8338/kurjun/rest/apt/upload | gpg --clearsign --no-tty", returnStdout: true)
            // sh "curl -s -k -Ftoken=${token} -Fsignature=\"${signatureDeb}\" https://${cdnHost}:8338/kurjun/rest/auth/sign"

            // delete old deb
            // if (responseDeb != "Not found") {
            //     def jsonDeb = jsonParse(responseDeb)
            //     sh """
			// 	set +x
			// 	curl -s -k -X DELETE https://${cdnHost}:8338/kurjun/rest/apt/delete?id=${jsonDeb[0]["id"]}'&'token=${
            //         token
            //     }
			// """
            //}

            // upload template
            String responseTemplate = sh(script: """
			set +x
			curl -s -k https://${cdnHost}:8338/kurjun/rest/template/info?name=management'&'version=${env.BRANCH_NAME}
			""", returnStdout: true)
            def signatureTemplate = sh(script: """
			set +x
			curl -s -k -Ffile=@${templateFileName} -Ftoken=${token} -H "token: ${token}" https://${cdnHost}:8338/kurjun/rest/template/upload | gpg --clearsign --no-tty
			""", returnStdout: true)
            sh """
			set +x
			curl -s -k -Ftoken=${token} -Fsignature=\"${signatureTemplate}\" https://${cdnHost}:8338/kurjun/rest/auth/sign
		    """
            sh """
            set +x
            echo "https://${cdnHost}:8338/kurjun/rest/template/list"
            """

            // delete old template
            if (responseTemplate != "Not found") {
                def jsonTemplate = jsonParse(responseTemplate)
                sh """
				set +x
				curl -s -k -X DELETE https://${cdnHost}:8338/kurjun/rest/template/delete?id=${
                    jsonTemplate[0]["id"]
                }'&'token=${token}
			"""
            }
        }
    } catch (e) {
        currentBuild.result = "FAILED"
        throw e
    } finally {
        // Success or failure, always send notifications
        notifyBuild(currentBuild.result, notifyBuildDetails)
    }
}

def getVersionFromPom(pom) {
    def matcher = readFile(pom) =~ '<version>(.+)</version>'
    matcher ? matcher[1][1] : null
}

def String getVersion(pom) {
    def pomver = getVersionFromPom(pom)
    def ver = sh(script: "/bin/echo ${pomver} | cut -d '-' -f 1", returnStdout: true)
    return "${ver}".trim()
}

@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

// https://jenkins.io/blog/2016/07/18/pipline-notifications/
def notifyBuild(String buildStatus = 'STARTED', String details = '') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = "${subject} (${env.BUILD_URL})"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
        summary = "${subject} (${env.BUILD_URL})${details}"
    }
    // Get token
    def slackToken = getSlackToken('ss-bots')
    // Send notifications
    slackSend(color: colorCode, message: summary, teamDomain: 'optdyn', token: "${slackToken}")
}

// get slack token from global jenkins credentials store
@NonCPS
def getSlackToken(String slackCredentialsId) {
    // id is ID of creadentials
    def jenkins_creds = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]

    String found_slack_token = jenkins_creds.getStore().getDomains().findResult { domain ->
        jenkins_creds.getCredentials(domain).findResult { credential ->
            if (slackCredentialsId.equals(credential.id)) {
                credential.getSecret()
            }
        }
    }
    return found_slack_token
}
