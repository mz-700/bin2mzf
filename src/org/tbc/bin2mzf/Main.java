package org.tbc.bin2mzf;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main {

    private final static Options options = new Options();

    public static void main( String[] args ) {

        int z= 1;

        options.addRequiredOption( "f", "file", true, "binary input file" );
        addOption( "d", "directory", "output directory" );
        addOption( "o", "output", "mzf output file" );
        addOption( "l", "loadaddr", "load address" );
        addOption( "s", "startaddr", "start address" );
        addOption( "t", "title", "title" );
        addOption( "c", "comments", "comments" );

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse( options, args );
        }
        catch ( Exception e ) {
            print( e.getMessage());
            formatter.printHelp( "bin2mzf", options );
            err();
        }

        String inFilename = cmd.getOptionValue( "file" );
        String outFilename = cmd.getOptionValue( "output" );
        if ( outFilename == null ) { outFilename = StringUtils.substringBeforeLast( inFilename, ".") + ".mzf";   }

        try {
            String dir = cmd.getOptionValue( "directory" );
            File fin = dir == null ? new File( inFilename ) : new File( dir, inFilename );
            int filesize = (int)fin.length();

            if ( filesize > 0xD000 - 0x1200 ) {
                print( "Error: File is too big. Max size is 48640 bytes." );
                err();
            }

            Integer loadAddr = parseInt( cmd.getOptionValue( "loadaddr" ) );
            if ( loadAddr == null  ) { loadAddr = 0x1200; }
            if ( loadAddr < 0x1200 || loadAddr > 0xD000 - filesize  ) {
                print( "Error: Invalid load address. Must be between $1200 and $" + Integer.toHexString( 0xD000 - filesize ) );
                err();
            }

            Integer startAddress = parseInt( cmd.getOptionValue( "startaddr" ) );
            if ( startAddress == null ) { startAddress = 0x1200; }
            if ( startAddress < loadAddr || startAddress > loadAddr + filesize ) {
                print( "Error: Invalid start address. Must be between $" + Integer.toHexString( loadAddr ) + " and $" + Integer.toHexString( loadAddr + filesize ) );
                err();
            }

            FileInputStream fis = new FileInputStream( fin );
            FileOutputStream fos = dir == null ? new FileOutputStream( outFilename ) : new FileOutputStream( new File( dir, outFilename ) );
            fos.write( 1 );         // binary file
            String title = cmd.getOptionValue( "title" );
            if ( title == null ) { title = StringUtils.substringBeforeLast( fin.getName(), "." ); }
            for ( char i : formatString( title, 16 ) ) { fos.write( i ); }
            fos.write( 0x0D );
            fos.write( getLsb( filesize ) );
            fos.write( getMsb( filesize ) );
            fos.write( getLsb( loadAddr ) );
            fos.write( getMsb( loadAddr ) );
            fos.write( getLsb( startAddress ) );
            fos.write( getMsb( startAddress ) );
            for ( char i : formatString( cmd.getOptionValue( "comments" ), 104 ) ) { fos.write( i ); }
            for ( int i = 0; i < filesize; i++ ) { fos.write( fis.read() ); }
            fis.close();
            fos.close();
        }
        catch ( Exception e ) {
            print( e.getMessage() );
            System.exit( 1 );
        }
    }

    private static Option addOption( String option, String longOption, String description ) {
        Option o = new Option( option, longOption, true, description );
        options.addOption( o );
        return o;
    }

    private static int getLsb( int n ) {
        return n & 0xFF;
    }

    private static int getMsb( int n ) {
        return ( n >> 8 ) & 0xFF;
    }

    private static Integer parseInt( String s ) {
        if ( s == null ) { return null; }
        try { return s.startsWith( "$" ) ?   Integer.parseInt( s.substring( 1 ), 16 ) : Integer.parseInt( s ); }
        catch ( Exception e ) { return null; }
    }

    private static void print( String s ) {
        System.out.println( s );
    }

    private static void err() {
        System.exit( 1 );
    }

    private static char[] formatString( String s, int n ) {
        s = StringUtils.rightPad( s == null ? "" : StringUtils.left( s, n ), n, ' ');
        return s.toUpperCase().toCharArray();
    }
}
