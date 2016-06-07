package com.github.startup.systemconfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;


public class SystemProperties {
	private static Log log = LogFactory.getLog( SystemProperties.class );
    private static String FILE_NAME = "/system.properties"; //文件名
    private static final Properties conf_PROPERTIES;
    static
    {
    conf_PROPERTIES = new Properties();
    InputStream stream = SystemProperties.class.getResourceAsStream(FILE_NAME);
    if ( stream == null ) {
      log.warn( FILE_NAME + " not found" );
    }
    else {
      try {
        conf_PROPERTIES.load( stream );
        log.info( "loaded properties from resource " + FILE_NAME + ": " + conf_PROPERTIES );
      }
      catch ( Exception e ) {
        log.error( "problem loading properties from " + FILE_NAME );
      }
      finally {
        try {
          stream.close();
        }
        catch ( IOException ioe ) {
          log.error( "could not close stream on " + FILE_NAME, ioe );
        }
      }
    }
  }
  public static Properties getProperties()
  {
    return conf_PROPERTIES;
  }

  public static void save()
  {
    try {
      URL url = SystemProperties.class.getResource( FILE_NAME);
      String filePath = java.net.URLDecoder.decode( url.getFile(), "utf-8" );
      log.info( "saving properties to :" + filePath );
      FileOutputStream fout = new FileOutputStream( filePath );
      DataOutputStream out = new DataOutputStream( fout );
      conf_PROPERTIES.store( out, "系统保存" );
    }
    catch ( FileNotFoundException ex ) {
      log.warn( ex.getMessage() );
    }
    catch ( UnsupportedEncodingException ex ) {
      log.warn( ex.getMessage() );
    }
    catch ( IOException ex ) {
      log.warn( ex.getMessage() );
    }
  }
  public static String get( String key )
  {
    return conf_PROPERTIES.getProperty( key );
  }
  public static int getIntValue( String key )
  {
    return getIntValue( key, 0 );
  }
  public static int getIntValue( String key, int defaultValue )
  {
    String str = conf_PROPERTIES.getProperty( key );
    int value = 0;
    try {
      value = Integer.parseInt( str );
    }
    catch ( NumberFormatException ex ) {
      value = defaultValue;
    }
    return value;
  }
  public static void main( String[] args )
  {
    //String tmpAry[][] = getGxtvProductAry();
  }
}
