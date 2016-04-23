import java.math.BigDecimal;

/**
 *
 * @author Stiaan Uyttersprot
 * 
 */
public class Vector {
	private BigDecimal xCoord;
	private BigDecimal yCoord;
	private BigDecimal vectorAngle;
	//the number of the edge that the vector slides over
	private int edgeNumber;

	private boolean polygonA;

	private Edge parentEdge;
	
	Vector(BigDecimal x, BigDecimal y) {
		xCoord = x;
		yCoord = y;
	}

	public Vector(Vector vect) {
		xCoord = vect.getxCoord();
		yCoord = vect.getyCoord();
		edgeNumber = vect.getEdgeNumber();
		calculateVectorAngle();
	}

	public Vector(Coordinate coord, int eN, boolean polygonA) {
		xCoord = coord.getxCoord();
		yCoord = coord.getyCoord();
		calculateVectorAngle();
		edgeNumber = eN;
		this.setPolygonA(polygonA);
	}

	public Vector(Coordinate startPoint, Coordinate endPoint) {
		
		Coordinate vectorCoord = endPoint.subtract(startPoint);
		xCoord = vectorCoord.getxCoord();
		yCoord = vectorCoord.getyCoord();
		calculateVectorAngle();
		edgeNumber = -1;
	}

	public BigDecimal getxCoord() {
		return xCoord;
	}

	public void setxCoord(BigDecimal xCoord) {
		this.xCoord = xCoord;
	}

	public BigDecimal getyCoord() {
		return yCoord;
	}

	public void setyCoord(BigDecimal yCoord) {
		this.yCoord = yCoord;
	}

	public BigDecimal getVectorAngle() {
		return vectorAngle;
	}

	public void setVectorAngle(BigDecimal vectorAngle) {
		this.vectorAngle = vectorAngle;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	public boolean equals(Vector vect) {
		if(xCoord.compareTo(vect.getxCoord()) != 0) return false;
		if(yCoord.compareTo(vect.getyCoord())!=0) return false;
		return true;
	}

	// D-function is used to calculate where a point is located in reference to
	// a vector
	// if the value is larger then 0 the point is on the left
	// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	public BigDecimal dFunction(Vector startPoint, Vector endPoint) {

		BigDecimal dxab = startPoint.getxCoord().subtract(endPoint.getxCoord());
		BigDecimal dyap = startPoint.getyCoord().subtract(yCoord);
		BigDecimal dyab = startPoint.getyCoord().subtract(endPoint.getyCoord());
		BigDecimal dxap = startPoint.getxCoord().subtract(xCoord);
		
		BigDecimal dValue = dxab.multiply(dyap).subtract(dyab.multiply(dxap));
		
		return dValue;
	}

	public void move(BigDecimal x, BigDecimal y) {
		xCoord = xCoord.add(x);
		yCoord = yCoord.add(y);
	}

	//check if two vectors are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Vector vect) {

		if(xCoord.abs().compareTo(BigDecimal.ZERO)==0)xCoord = xCoord.abs();
		if(yCoord.abs().compareTo(BigDecimal.ZERO)==0)yCoord = yCoord.abs();
		
		if(vect.getxCoord().abs().compareTo(BigDecimal.ZERO)==0)vect.setxCoord(vect.getxCoord().abs());
		if(vect.getyCoord().abs().compareTo(BigDecimal.ZERO)==0)vect.setyCoord(vect.getyCoord().abs());
		
		if (xCoord.compareTo(vect.getxCoord())==0)
			return false;
		if (yCoord.compareTo(vect.getyCoord())==0)
			return false;
		return true;
	}

	// this vector minus the given vector
	public Vector subtract(Vector point) {

		return new Vector(xCoord.subtract(point.getxCoord()), yCoord.subtract(point.getyCoord()));
	}

	public Vector add(Vector point) {

		return new Vector(xCoord.add(point.getxCoord()), yCoord.add(point.getyCoord()));
	}

	public boolean isBiggerThen(Vector biggestCoord) {

		return false;
	}

	private void calculateVectorAngle() {

		vectorAngle = new BigDecimal(Math.atan2(yCoord.doubleValue(),xCoord.doubleValue()));
	}

	public BigDecimal getLengthSquared() {
		
		return xCoord.multiply(xCoord).add(yCoord.multiply(yCoord));
	}

	public Edge getParentEdge() {
		return parentEdge;
	}

	public void setParentEdge(Edge parentEdge) {
		this.parentEdge = parentEdge;
	}

	public boolean isPolygonA() {
		return polygonA;
	}

	public void setPolygonA(boolean polygonA) {
		this.polygonA = polygonA;
	}

	@Override
	public String toString() {
		return "Vector [xCoord=" + xCoord + ", yCoord=" + yCoord + ", vectorAngle=" + vectorAngle + ", edgeNumber="
				+ edgeNumber + ", polygonA=" + polygonA + ", parentEdge=" + parentEdge + "]";
	}

	 /**
     * Compute the arctangent of x to a given scale, |x| < 1
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    public static BigDecimal arctan(BigDecimal x, int scale)
    {
        // Check that |x| < 1.
        if (x.abs().compareTo(BigDecimal.valueOf(1)) >= 0) {
            throw new IllegalArgumentException("|x| >= 1");
        }

        // If x is negative, return -arctan(-x).
        if (x.signum() == -1) {
            return arctan(x.negate(), scale).negate();
        }
        else {
            return arctanTaylor(x, scale);
        }
    }

    /**
     * Compute the arctangent of x to a given scale
     * by the Taylor series, |x| < 1
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    private static BigDecimal arctanTaylor(BigDecimal x, int scale)
    {
        int     sp1     = scale + 1;
        int     i       = 3;
        boolean addFlag = false;

        BigDecimal power = x;
        BigDecimal sum   = x;
        BigDecimal term;

        // Convergence tolerance = 5*(10^-(scale+1))
        BigDecimal tolerance = BigDecimal.valueOf(5)
                                            .movePointLeft(sp1);

        // Loop until the approximations converge
        // (two successive approximations are within the tolerance).
        do {
            // x^i
            power = power.multiply(x).multiply(x)
                        .setScale(sp1, BigDecimal.ROUND_HALF_EVEN);

            // (x^i)/i
            term = power.divide(BigDecimal.valueOf(i), sp1,
                                 BigDecimal.ROUND_HALF_EVEN);

            // sum = sum +- (x^i)/i
            sum = addFlag ? sum.add(term)
                          : sum.subtract(term);

            i += 2;
            addFlag = !addFlag;

            Thread.yield();
        } while (term.compareTo(tolerance) > 0);

        return sum;
    }
	
}
