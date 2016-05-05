package JNFP;


import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class contains methods for generating the NFP given two files and returns a string describing the NFP.
 * 
 * @author Stiaan Uyttersprot
 *
 */
public class JNFP {

	/**
	 * Generate the NFP using the orbiting method using a fixed round (1e-4).
	 * This is the general method, the orbiting method provides the best results, so it is also used here.
	 * @param statPolyFile the stationary polygon file
	 * @param orbPolyFile the orbiting polygon file
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateNFP(File statPolyFile, File orbPolyFile) throws FileNotFoundException{
		Orbiting.adjustRound(1e-4);
		MultiPolygon statPoly = new MultiPolygon(statPolyFile);
		MultiPolygon orbPoly = new MultiPolygon(orbPolyFile);
		
		String nfp = Orbiting.generateNFP(statPoly, orbPoly);
		return nfp;
	}
	
	/**
	 * Generate the NFP using the orbiting method using a fixed round (1e-4)
	 * @param statPolyFile the stationary polygon file
	 * @param orbPolyFile the orbiting polygon file
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateOrbitingNFP(File statPolyFile, File orbPolyFile) throws FileNotFoundException{
		Orbiting.adjustRound(1e-4);
		MultiPolygon statPoly = new MultiPolygon(statPolyFile);
		MultiPolygon orbPoly = new MultiPolygon(orbPolyFile);
		String nfp = Orbiting.generateNFP(statPoly, orbPoly);
		return nfp;
	}
	/**
	 * Generate the NFP using the Minkowski sums method using a fixed round (1e-1)
	 * @param polyAFile polygon A
	 * @param polyBFile polygon B
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateMinkowskiNFP(File polyAFile, File polyBFile) throws FileNotFoundException{
		Minkowski.adjustRound(1);
		MultiPolygon polyA = new MultiPolygon(polyAFile);
		MultiPolygon polyB = new MultiPolygon(polyBFile);
		
		NoFitPolygon nfp = Minkowski.generateMinkowskiNFP(polyA, polyB);
		return nfp.toString();
	}
	
	/**
	 * Generate the NFP using the orbiting method using a fixed round (1e-4).
	 * This is the general method, the orbiting method provides the best results, so it is also used here.
	 * @param statPoly the stationary polygon
	 * @param orbPoly the orbiting polygon 
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateNFP(MultiPolygon statPoly, MultiPolygon orbPoly) throws FileNotFoundException{
		Orbiting.adjustRound(1e-4);
		
		String nfp = Orbiting.generateNFP(statPoly, orbPoly);
		return nfp;
	}
	
	/**
	 * Generate the NFP using the orbiting method using a fixed round (1e-4)
	 * @param statPoly the stationary polygon
	 * @param orbPoly the orbiting polygon
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateOrbitingNFP(MultiPolygon statPoly, MultiPolygon orbPoly) throws FileNotFoundException{
		Orbiting.adjustRound(1e-4);
		String nfp = Orbiting.generateNFP(statPoly, orbPoly);
		return nfp;
	}
	/**
	 * Generate the NFP using the Minkowski sums method using a fixed round (1e-1)
	 * @param polyA polygon A
	 * @param polyB polygon B
	 * @return the NFP in string form
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String GenerateMinkowskiNFP(MultiPolygon polyA, MultiPolygon polyB) throws FileNotFoundException{
		Minkowski.adjustRound(0.1);
		NoFitPolygon nfp = Minkowski.generateMinkowskiNFP(polyA, polyB);
		return nfp.toString();
	}
}
