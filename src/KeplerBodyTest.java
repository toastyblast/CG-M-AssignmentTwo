import java.util.ArrayList;

public class KeplerBodyTest {
    private final StdDraw3D.Shape planet, path;
    private final KeplerBodyTest parent;

    private final double semimajorAxis, inclination, obliquity, radius, equatorRotationDegrees, eccentricity, eclipticLongtitude, omegaPerihelion;
    private final double degreePerMs, meanAnomalyJ2000;
    private double distParent;
    private long timeInMs;

    private final ArrayList<KeplerBodyTest> children;

    public KeplerBodyTest(double diameter, double hoursFullEquatorRotation, double obliquity, KeplerBodyTest parent, double perihelion, double eccentricity, double omegaPerihelion, double eclipticLongtitude, double inclination, double meanAnomalyJ2000) {
        radius = (diameter / 149598000.0) / 2.0;
        equatorRotationDegrees = calculateRotationDegree(hoursFullEquatorRotation);
        this.obliquity = obliquity;
        this.eccentricity = eccentricity;
        semimajorAxis = perihelion / (1.0 - eccentricity);
        this.inclination = inclination;
        this.eclipticLongtitude = eclipticLongtitude;
        this.omegaPerihelion = omegaPerihelion;
        this.meanAnomalyJ2000 = meanAnomalyJ2000;
//        degreePerMs = (.9856076686 / Math.pow(semimajorAxis, (3 / 2))) / 86400000.0;
        degreePerMs = .9856076686 / Math.pow(semimajorAxis, (3.0 / 2.0));

        this.parent = parent;
        children = new ArrayList<>();

        planet = StdDraw3D.sphere(0, 0, 0, radius, -90, obliquity, 0);
        timeInMs = System.currentTimeMillis();
        movePlanet();
        if (parent != null) {
            path = drawPath();
            parent.addChild(this);
        } else {
            path = null;
        }
    }

    public KeplerBodyTest(double diameter, double hoursFullEquatorRotation, double obliquity, KeplerBodyTest parent, double perihelion, double eccentricity, double omegaPerihelion, double eclipticLongtitude, double inclination, double meanAnomalyJ2000, String textureURL) {
        radius = (100.0 * (KeplerSolarSystemTest.pixelsPerAU * (diameter / 149598000.0))) / 2.0;
        equatorRotationDegrees = calculateRotationDegree(hoursFullEquatorRotation);
        this.obliquity = obliquity;
        this.eccentricity = eccentricity;
        semimajorAxis = (perihelion / 149.598) / (1.0 - eccentricity);
        this.inclination = inclination;
        this.eclipticLongtitude = eclipticLongtitude;
        this.omegaPerihelion = omegaPerihelion;
        this.meanAnomalyJ2000 = meanAnomalyJ2000;
        degreePerMs = (.9856076686 / Math.pow(semimajorAxis, (3.0 / 2.0))) / 86400000.0;
//        degreePerMs = .9856076686 / Math.pow(semimajorAxis, (3.0 / 2.0));

        this.parent = parent;
        children = new ArrayList<>();

        planet = StdDraw3D.sphere(0, 0, 0, radius, -90, obliquity, 0, textureURL);
//        timeInMs = 1072915200 / 86400;
        timeInMs = System.currentTimeMillis();
        movePlanet();
        if (parent != null) {
            path = drawPath();
            parent.addChild(this);
        } else {
            path = null;
        }
    }

    public StdDraw3D.Vector3D getPosition() { return planet.getPosition(); }

    private double calculateRotationDegree(double hoursFullEquatorRotation) {
        double millisecondsFullEquatorRotation = hoursFullEquatorRotation * 3600000.0;
        double amountOfShifts = millisecondsFullEquatorRotation / ((double)(KeplerSolarSystemTest.timeShiftMilliseconds) * (double)(KeplerSolarSystemTest.timeAmplifier));
        return 360.0 / amountOfShifts;
    }
    public double getEquatorRotationDegrees() { return equatorRotationDegrees; }

    public void movePlanet() {
        // Rotate the planet around its own axis.
        planet.rotateRelative(0, equatorRotationDegrees, 0);

        if (parent != null) {
            // 946684800 is the amount of seconds from Epoch 1970 till J2000 (1/1/2000 0:0:0 UTC).
            System.out.println("Degrees per day: " + degreePerMs);
            System.out.println("Time in days: " + timeInMs);
            double meanAnomaly = meanAnomalyJ2000 + degreePerMs * ((timeInMs - (946684800.0 / 86400.0)));
            while (meanAnomaly < 0.0) {
                meanAnomaly += 360.0;
            }
            if (meanAnomaly > 360.0) {
                meanAnomaly = meanAnomaly % 360.0;
            }
            System.out.println("Mean anomaly: " + meanAnomaly);

            double meanAnomalyRadians = Math.toRadians(meanAnomaly);
            System.out.println("Mean anomaly in radians: " + meanAnomalyRadians);
            double eccentricAnomaly = meanAnomalyRadians + eccentricity * (180.0 / Math.PI) * Math.sin(meanAnomalyRadians) * (1.0 + eccentricity * Math.cos(meanAnomalyRadians));
            System.out.println("Eccentric anomaly: " + eccentricAnomaly);

            double xv = semimajorAxis * (Math.cos(eccentricAnomaly) - eccentricity);
            System.out.println("xv: " + xv);
            double yv = semimajorAxis * (Math.sqrt(1.0 - (eccentricity * eccentricity)) * Math.sin(eccentricAnomaly));
            System.out.println("yv: " + yv);
            double trueAnomaly = meanAnomalyRadians + Math.atan2(yv, xv);
            System.out.println("True anomaly: " + trueAnomaly);
            double special = semimajorAxis * (1.0 - (eccentricity * eccentricity));
            System.out.println("a * (1 - e^2) = " + special);
            distParent = special / (1.0 + eccentricity * Math.cos(trueAnomaly));
            System.out.println("Distance from parent: " + distParent);
            distParent = distParent * KeplerSolarSystemTest.pixelsPerAU;

            double x = distParent * (Math.cos(eclipticLongtitude) * Math.cos(omegaPerihelion + trueAnomaly) - Math.sin(eclipticLongtitude) * Math.cos(inclination) * Math.sin(omegaPerihelion + trueAnomaly));
            double y = distParent * (Math.cos(eclipticLongtitude) * Math.cos(omegaPerihelion + trueAnomaly) + Math.sin(eclipticLongtitude) * Math.cos(inclination) * Math.sin(omegaPerihelion + trueAnomaly));
            double z = distParent * (Math.sin(inclination) * Math.sin(omegaPerihelion + trueAnomaly));

            planet.setPosition((parent.getPosition().x + x), (parent.getPosition().y + y), z);
            System.out.println("Position: " + planet.getPosition() + "\n");
            timeInMs += KeplerSolarSystemTest.timeShiftMilliseconds * KeplerSolarSystemTest.timeAmplifier;
        }

        // Then move every child this planet has as well.
        if (children.size() > 0) {
            for (KeplerBodyTest child : children) {
                child.movePlanet();
            }
        }
    }

    private void addChild(KeplerBodyTest child) {
        children.add(child);
    }

    private StdDraw3D.Shape drawPath() {
        // Calculate by the size of the path radius how many dots we want to draw. The smaller the radius, the less dots should be drawn.
        int amountOfPoints = (int)(Math.round(2 * distParent));

        StdDraw3D.Shape[] pathPoints = new StdDraw3D.Shape[amountOfPoints];
        for (int i = 1; i <= amountOfPoints; i++) {
            double angleRad = i * (Math.PI / (amountOfPoints / 2)); // Convert angle to radians
            // Then for every point in numberOfPoints, calculate the X and Y values for each of the points.
            double pointX = Math.cos(angleRad) * distParent - Math.sin(angleRad) * planet.getPosition().y;
            double pointY = Math.sin(angleRad) * distParent + Math.cos(angleRad) * planet.getPosition().y;
            double pointZ = 0;

            pathPoints[i - 1] = StdDraw3D.point(pointX, pointY, pointZ);
        }

        // Combine all the dots together to get one shape: A circle of dots.
        StdDraw3D.Shape path = StdDraw3D.combine(pathPoints);
        // Now set the center of the circle of dots to be the center of the parent planet.
        StdDraw3D.Vector3D parentPosition = parent.getPosition();
        path.setPosition(parentPosition.x, parentPosition.y, parentPosition.z);

        // If the planet orbits at an angle around the parent, the path should show this.
        if (inclination != 0) { path.rotate(0, -inclination, 0); }

        return path;
    }
}
