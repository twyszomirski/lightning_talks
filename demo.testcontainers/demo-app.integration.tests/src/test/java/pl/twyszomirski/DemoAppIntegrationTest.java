package pl.twyszomirski;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

public class DemoAppIntegrationTest{

    private final static Logger LOG = LoggerFactory.getLogger( DemoAppIntegrationTest.class.getName() );

    private static final String MYSQL_ROOT_PASSWORD = "demo";
    private static final String MYSQL_DATABASE = "demo";
    private static final String MYSQL_USER = "demo";
    private static final String MYSQL_PASSWORD = "demo";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    private HikariDataSource hikariDataSource;


    @Before
    public void init(){
        setupDataSource();
    }

    @Rule
    public Network network = Network.SHARED;

    @Rule
    public GenericContainer mysql = new GenericContainer( "mysql:5.7" )
        .withNetwork( network )
        .withNetworkAliases( "demo_mysql" )
        .withEnv( "MYSQL_ROOT_PASSWORD", "demo" )
        .withEnv( "MYSQL_DATABASE", "demo" )
        .withEnv( "MYSQL_USER", "demo" )
        .withEnv( "MYSQL_PASSWORD", "demo" )
        .withExposedPorts( 3306 );


    @Rule
    public GenericContainer app = new GenericContainer( "pl/twyszomirski/demo-app.docker.image:0.0.1" )
        .withNetwork( network )
        .withNetworkAliases( "demo_app" )
        .withCommand( "java","-Djava.security.egd=file:/dev/./urandom","-jar","/demo-app.jar" )
        .withExposedPorts( 8080 )
        .withLogConsumer( new Slf4jLogConsumer( LOG ) );



    @Test
    public void doTest() throws IOException, SQLException
    {
        int appPort = app.getMappedPort( 8080 );
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute( new HttpGet( "http://localhost:"+appPort + "/demo/hello"));

        Assert.assertThat( response.getStatusLine().getStatusCode(), is( HttpStatus.SC_OK ) );

        ResultSet resultSet = performDatabaseQuery( "select count(*) from log_entry" );
        Assert.assertThat( resultSet.getInt( 1 ), is( 1 ) );

        response = client.execute( new HttpGet( "http://localhost:"+appPort + "/demo/hello"));

        Assert.assertThat( response.getStatusLine().getStatusCode(), is( HttpStatus.SC_OK ) );

        resultSet = performDatabaseQuery( "select count(*) from log_entry" );
        Assert.assertThat( resultSet.getInt( 1 ), is( 2 ) );

    }

    private void setupDataSource(){
        Integer mysqlPort = mysql.getMappedPort( 3306 );

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName( MYSQL_DRIVER );
        hikariConfig.setJdbcUrl( "jdbc:mysql://localhost:" + mysqlPort + "/"+MYSQL_DATABASE );
        hikariConfig.setUsername( MYSQL_USER );
        hikariConfig.setPassword( MYSQL_PASSWORD );

        hikariDataSource = new HikariDataSource( hikariConfig );
    }

    protected ResultSet performDatabaseQuery( String aSql ) throws SQLException
    {
        Statement statement = hikariDataSource.getConnection().createStatement();
        statement.execute( aSql );
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        return resultSet;
    }
}