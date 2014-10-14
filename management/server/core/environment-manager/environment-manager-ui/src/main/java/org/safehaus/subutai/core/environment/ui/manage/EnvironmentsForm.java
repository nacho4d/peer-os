package org.safehaus.subutai.core.environment.ui.manage;


import java.util.List;
import java.util.Set;

import org.safehaus.subutai.common.exception.ContainerException;
import org.safehaus.subutai.common.protocol.Container;
import org.safehaus.subutai.common.protocol.DefaultCommandMessage;
import org.safehaus.subutai.core.environment.api.EnvironmentContainer;
import org.safehaus.subutai.core.environment.api.exception.EnvironmentDestroyException;
import org.safehaus.subutai.core.environment.api.helper.Environment;
import org.safehaus.subutai.core.environment.ui.EnvironmentManagerPortalModule;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


@SuppressWarnings( "serial" )
public class EnvironmentsForm
{

    private static final String DESTROY = "Destroy";
    private static final String VIEW = "View";
    private static final String MANAGE = "Manage";
    private static final String CONFIGURE = "Configure";
    private static final String NAME = "Name";
    private static final String ENVIRONMENTS = "Environments";
    private static final String PROPERTIES = "Properties";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final String MANAGE_TITLE = "Manage environment containers";
    private VerticalLayout contentRoot;
    private Table environmentsTable;
    private EnvironmentManagerPortalModule managerUI;
    private Button environmentsButton;


    public EnvironmentsForm( final EnvironmentManagerPortalModule managerUI )
    {
        this.managerUI = managerUI;

        contentRoot = new VerticalLayout();
        contentRoot.setSpacing( true );
        contentRoot.setMargin( true );

        environmentsTable = createTable( ENVIRONMENTS, 300 );

        environmentsButton = new Button( VIEW );

        environmentsButton.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                updateTableData();
            }
        } );


        contentRoot.addComponent( environmentsButton );
        contentRoot.addComponent( environmentsTable );
    }


    private Table createTable( String caption, int size )
    {
        Table table = new Table( caption );
        table.addContainerProperty( NAME, String.class, null );
        table.addContainerProperty( MANAGE, Button.class, null );
        table.addContainerProperty( CONFIGURE, Button.class, null );
        table.addContainerProperty( DESTROY, Button.class, null );
        table.setPageLength( 10 );
        table.setSelectable( false );
        table.setEnabled( true );
        table.setImmediate( true );
        table.setSizeFull();
        return table;
    }


    private void updateTableData()
    {
        environmentsTable.removeAllItems();
        List<Environment> environmentList = managerUI.getEnvironmentManager().getEnvironments();
        for ( final Environment environment : environmentList )
        {
            Button manageButton = new Button( MANAGE );
            manageButton.addClickListener( new Button.ClickListener()
            {
                @Override
                public void buttonClick( final Button.ClickEvent clickEvent )
                {
                    Window window = envWindow( environment );
                    window.setVisible( true );
                }
            } );

            Button destroyButton = new Button( DESTROY );
            destroyButton.addClickListener( new Button.ClickListener()
            {
                @Override
                public void buttonClick( final Button.ClickEvent clickEvent )
                {
                    try
                    {
                        managerUI.getEnvironmentManager().destroyEnvironment( environment.getUuid().toString() );
                        environmentsButton.click();
                    }
                    catch ( EnvironmentDestroyException e )
                    {
                        Notification.show( e.getMessage() );
                    }
                }
            } );
            Button configureButton = new Button( CONFIGURE );
            configureButton.addClickListener( new Button.ClickListener()
            {
                @Override
                public void buttonClick( final Button.ClickEvent clickEvent )
                {
                    Notification.show( CONFIGURE );
                }
            } );

            environmentsTable.addItem( new Object[] {
                    environment.getName() + " " + environment.getUuid(), manageButton, configureButton, destroyButton
            }, environment.getUuid() );
        }
        environmentsTable.refreshRowCache();
    }


    private Window envWindow( Environment environment )
    {
        Window window = createWindow( MANAGE_TITLE );
        window.setContent( genContainersTable(environment,  environment.getContainers() ) );
        contentRoot.getUI().addWindow( window );
        return window;
    }


    private Window createWindow( String caption )
    {
        Window window = new Window();
        window.setCaption( caption );
        window.setWidth( "800px" );
        window.setHeight( "600px" );
        window.setModal( true );
        window.setClosable( true );
        return window;
    }


    private VerticalLayout genContainersTable( Environment environment, Set<EnvironmentContainer> containers )
    {
        VerticalLayout vl = new VerticalLayout();

        Table containersTable = new Table();
        containersTable.addContainerProperty( NAME, String.class, null );
        containersTable.addContainerProperty( PROPERTIES, Button.class, null );
        containersTable.addContainerProperty( START, Button.class, null );
        containersTable.addContainerProperty( STOP, Button.class, null );
        containersTable.addContainerProperty( DESTROY, Button.class, null );
        containersTable.setPageLength( 10 );
        containersTable.setSelectable( false );
        containersTable.setEnabled( true );
        containersTable.setImmediate( true );
        containersTable.setSizeFull();

        for ( Container container : containers )
        {

            containersTable.addItem( new Object[] {
                    container.getName() + " on " + container.getPeerId(), propertiesButton( container ),
                    startButton( environment, container ), stopButton( environment, container ), destroyButton( environment, container )
            }, null );
        }


        vl.addComponent( containersTable );
        return vl;
    }


    private Object propertiesButton( final Container container )
    {
        Button button = new Button( PROPERTIES );
        button.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                Window window = createWindow( PROPERTIES );
                window.setContent( getContainerDetails( container ) );
                window.setWidth( "600px" );
                window.setHeight( "300px" );
                contentRoot.getUI().addWindow( window );
                window.setVisible( true );
                //                Notification.show( PROPERTIES );
            }
        } );
        return button;
    }


    private Table getContainerDetails( Container container )
    {
        Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty( "Property", String.class, null );
        table.addContainerProperty( "Value", String.class, null );
        table.addItem( new Object[] { "Peer", container.getPeerId().toString() }, null );
        table.addItem( new Object[] { "Agent", container.getAgentId().toString() }, null );
        table.addItem( new Object[] { "IP", container.getIps().toString() }, null );
        table.addItem( new Object[] { "Environment ID", container.getEnvironmentId().toString() }, null );
        table.addItem( new Object[] { "Description", container.getDescription() }, null );
        return table;
    }


    private Object startButton( final Environment environment, final Container container )
    {
        Button button = new Button( START );
        button.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                try
                {
                    DefaultCommandMessage commandMessage = container.start();
                    environment.invoke( commandMessage );
                }
                catch ( ContainerException e )
                {
                    Notification.show( e.getMessage() );
                }
            }
        } );
        return button;
    }


    private Object stopButton( final Environment environment, final Container container )
    {
        Button button = new Button( STOP );
        button.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                try
                {

                    DefaultCommandMessage commandMessage = container.stop();
                    environment.invoke( commandMessage );
                }
                catch ( ContainerException e )
                {
                    Notification.show( e.getMessage() );
                }
            }
        } );
        return button;
    }


    private Object destroyButton( Environment environment, final Container container )
    {
        Button button = new Button( DESTROY );
        button.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                //TODO: destroy functionality
//                DefaultCommandMessage commandMessage = container.start();
//                environment.invoke( commandMessage );
                Notification.show( DESTROY );
            }
        } );
        return button;
    }


    public VerticalLayout getContentRoot()
    {
        return this.contentRoot;
    }
}
