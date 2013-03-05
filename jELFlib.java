import java . io . * ;
import java . math . BigInteger ;

/**
 * ELF
 * 
 * @author R3s1stanc3
 * @version 0.1
 */
public class ELF
{
    
    public static void main ( String [ ] args )
    {
        
        new ELF ( ) ;
        
    }
    
    public ELF ( )
    {
        
        byte [ ] fullFile = readFile ( "/home/r3s1stanc3/elf/halloadf" ) ;
        
        int arch = getArch ( fullFile ) ;
        // exit if we can't get the architecture or if the file is no ELF file
        if ( arch == 0 || !(isELF ( fullFile ))) System . exit ( 0 ) ;
        
        byte [ ] entryPoint = getEntryPoint ( fullFile, arch ) ;
        
        String hexString = byteToHex ( entryPoint ) ;
        int entry = Integer . parseInt ( hexString, 16 ) ;
        System . out . println ( byteToHex (intToByteArray(entry)) ) ;
        int programHeaderOffset = getProgramHeadersOffset ( fullFile, arch ) ;
        System . out . println ( hexString ) ;
        System . out . println ( entry ) ;
        System . out . println ( arch ) ;
        System . out . println ( programHeaderOffset ) ;
        
        // i used a hallo world ELF written in nasm and set the EntryPoint direct to the exit code
        byte [ ] newFile = changeEntryPoint ( fullFile, entry, 27 ) ;
        writeFileByte ( "/home/r3s1stanc3/elf/halloadf", newFile ) ;
        
    }
    
    /**
     * Reads the content of a file
     * @param name Name of the file
     * @return the files content as a byte array
     */
    public byte [ ] readFile ( String name )
    {
        
        try
        {
            RandomAccessFile file = new RandomAccessFile ( name, "r" ) ;
            byte [ ] data = new byte [ ( int ) file . length ( ) ] ;
            
            file . read ( data ) ;
            
            file . close ( ) ;
            
            return data ;
        }
        catch ( Exception e ) { return null ; }
        
    }
    
    /**
     * converts a byte array to a hex string
     * @param bytes byte array to convert
     * @return hex string
     */
    public String byteToHex ( byte [ ] bytes ) 
    {
        
        BigInteger bi = new BigInteger ( 1, bytes ) ;
        return String . format ( "%0" + ( bytes . length << 1 ) + "X", bi ) ;
        
    }
    
    /**
     * Returns the entry point of an ELF file.
     * @param file byte array of the whole file
     * @return entry point in a byte array
     */
    public byte [ ] getEntryPoint ( byte [ ] file, int arch )
    {
        
        int length ;
        if ( arch == 32 ) length = 4 ;
        else length = 8 ;
        
        byte [ ] entryPoint = new byte [ length ] ;
        for ( int i = 0; i < length; i ++ )
        {
            entryPoint [ i ] = file [ 0x18 + i ] ;
        }
        return reverse ( entryPoint ) ;
        
    }
    
    /**
     * checks the 4th byte of an ELF file to see if it is a 32 or 64 bit file
     * @param file byte array of the whole file
     * @return 32 - 32 bit; 64 - 64 bit; 0 - error
     */
    public int getArch ( byte [ ] file )
    {
        
        if ( file [ 0x4 ] == 0x01 ) return 32 ;
        else if ( file [ 0x4 ] == 0x02 ) return 64 ;
        else return 0 ;
        
    }
    
    /**
     * reverses a byte array
     * @param array array to reverse
     * @return reversed array
     */
    public byte [ ] reverse( byte [ ] array ) 
    {
        
        int i = 0 ;
        int j = array . length - 1 ;
        byte tmp ;
        while ( j > i ) 
        {
            tmp = array [ j ] ;
            array [ j ] = array [ i ] ;
            array [ i ] = tmp ;
            j-- ;
            i++ ;
        }
        return array ;
      
    }
    
    /**
     * checks the header of a file to see if it's a ELF or not
     * @param file byte array of the whole file
     * @retrun true - ELF file; false - no ELF file
     */
    public boolean isELF ( byte [ ] file )
    {
        
        // magic number of an ELF file: 0x7F454C46
        if ( file [ 0 ] == 0x7F && file [ 1 ] == 0x45 && file [ 2 ] == 0x4C && file [ 3 ] == 0x46 ) return true ;
        else return false ;
        
    }
    
    /**
     * reads the program header offset of an ELF file
     * @param file byte array of the whole file
     * @param arch architecture of the file
     * @return program header offset as an integer value
     */
    public int getProgramHeadersOffset ( byte [ ] file, int arch )
    {
        
        if ( arch == 32 ) return file [ 0x1C ] ;
        else return file [ 0x20 ] ;
        
    }
    
    /**
     * changes the entrypoint of a file by a given number
     * @param file byte array of the whole file
     * @param entry original entrypoint as integer value
     * @param change number to increase the entrypoint
     */
    public byte [ ] changeEntryPoint ( byte [ ] file, int entry, int change )
    {
        
        entry += change ;
        byte [ ] entryB = reverse ( intToByteArray ( entry ) ) ;
        for ( int i = 0; i < entryB . length; i ++ )
        {
            file [ 0x18 + i ] = entryB [ i ] ;
        }
        
        return file ;
        
    }
    
    /**
     * converts an integer value to a byte array of hex values
     * @param value integer value
     * @return byte array
     */
    public byte [ ] intToByteArray ( int value ) 
    {
        
        return new byte [ ] 
        {
            (byte)( value >>> 24 ),
            (byte)( value >>> 16 ),
            (byte)( value >>> 8 ),
            (byte) value
        };
        
    }
    
    /**
     * Writes a bytearray to a file
     * @param fi file to write to
     * @param data bytearray to write in the file
     */
    public void writeFileByte ( String fi, byte [ ] data ) 
    {
		try 
		{
	        FileOutputStream output = new FileOutputStream ( fi ) ;
	        output . write ( data, 0, data . length ) ;
	        output . flush ( ) ;
	        output . close ( ) ;
		}
		catch ( Exception e ) { }
	}
    
}
