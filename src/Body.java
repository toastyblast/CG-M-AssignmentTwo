import java.awt.*;
import java.util.ArrayList;

/**
 * Class that represents a body (star or planet) in a solar system. The values can be given as the real-life values, in the following formats:
 *  - diameter (double the radius of the planet) in kilometers (km). Dictates the size of the planet.
 *  - maxAngle (where Earth's is 0.0) in degrees. Dictates how much the planet moves up and down compared to the parent.
 *  - obliquity in degrees. Dictates how angled the axis of the planet is. Does not affect orbit.
 *  - rotatePeriod in hours. The amount of hours needed for the planet to spin 360 degrees on its axis.
 *  - orbitalPeriod in days. The amount of days needed for the planet to rotate around their parent once.
 *  - orbitHeight in km * 10e6. In the elliptical orbit, this is the height of the ellipse.
 *  - orbitWidth in km * 10e6. In the elliptical orbit, this is the width of the ellipse.
 *
 * @author Yoran Kerbusch (EHU Student 24143341)
 * Made for CIS2166 Computer Graphics & Modelling Coursework 2 (2018-2019)
 */
public class Body {
    private final String name;
    private final StdDraw3D.Shape planet, path, text;
    private final double orbitalPeriod, maxAngle, radius, rotatePeriod, orbitWidth, orbitHeight;
    private final ArrayList<Body> children;
    private final ArrayList<StdDraw3D.Shape> rings;
    private final Body parent;

    // Values that constantly change as the planet moves.
    private double rotationDegrees, frameOrbitDegree, currAngle, angleAlt, originalX, originalY;
    private double currOrbitDegree = 0;
    private boolean downwards = true;

    /**
     * Method that creates a Body with the given variables that is not textured, but instead just a coloured sphere.
     * To create a parent, give 0 for the following values: maxAngle, orbitalPeriod, orbitHeight & orbitWidth.
     * @param name String is the name of the Body.
     * @param diameter double is the diameter of the Body. This will be halved to create a radius.
     * @param maxAngle double is the angle at which the Body moved up and down in its orbit.
     * @param obliquity double is the tilt of the axis of the Body.
     * @param rotatePeriod double is the amount of hours needed for the Body to do one axis rotation.
     * @param orbitalPeriod double is the amount of days needed for the Body to rotate around the parent once.
     * @param parent Body is the parent of the Body. Optional, can be left out to create a parent Body, for instance.
     * @param orbitHeight double is the height of the orbit from the parent planet.
     * @param orbitWidth double is the width of the orbit from the parent planet.
     * @param color Color is the colour the planet must have.
     */
    public Body(String name, double diameter, double maxAngle, double obliquity, double rotatePeriod, double orbitalPeriod, Body parent, double orbitHeight, double orbitWidth, Color color) {
        this.name = name;
        this.rotatePeriod = rotatePeriod;
        this.orbitalPeriod = orbitalPeriod;
        this.children = new ArrayList<>();
        this.rings = new ArrayList<>();
        this.parent = parent;
        this.orbitWidth = (orbitWidth / 149.6) * SolarSystemCourseWork2.pixelsPerAU;
        this.orbitHeight = (orbitHeight / 149.6) * SolarSystemCourseWork2.pixelsPerAU;
        this.maxAngle = maxAngle;
        currAngle = maxAngle;

        // Create the planet as a sphere. WE will move it later, so first create it at the origin.
        radius = (diameter / SolarSystemCourseWork2.sizeScale) / 2.0;
        planet = StdDraw3D.sphere(0, 0, 0, radius, -90, obliquity, 0);

        StdDraw3D.setPenColor(color);
        text = StdDraw3D.text3D(0, 0, 0, name);
        text.scale(5.0);
        text.setPosition(planet.getPosition().x, 0, (planet.getPosition().z + radius + 1));

        if (parent != null) {
            calculateForTime(SolarSystemCourseWork2.timeShiftMilliseconds, SolarSystemCourseWork2.timeAmplifier);

            planet.setPosition(parent.getPosition());
            double[] startCoords = calculateNextPosition(this.orbitWidth, this.maxAngle);
            planet.move(startCoords[0], 0, startCoords[2]);
            originalX = parent.getPosition().x + startCoords[0];
            originalY = 0;

            path = drawDotsPath();
            parent.addChild(this);
        } else {
            // Because this is a planet without a parent, the orbitWidth, orbitHeight & maxAngle will instead serve as
            //  the (x, y, z) position of the body respectively. This is so that the user can position the parentless body wherever they want.
            planet.setPosition(orbitWidth, orbitHeight, maxAngle);
            path = null;

            calculateRotationDegree(rotatePeriod, SolarSystemCourseWork2.timeShiftMilliseconds, SolarSystemCourseWork2.timeAmplifier);
            // If it doesn't have a parent, the simulation will have to call it, since there is no parent to do that.
            SolarSystemCourseWork2.rootBodies.add(this);
        }
        StdDraw3D.setPenColor(Color.WHITE);
    }

    /**
     * Exactly the same as the Body above, only this one allows the user to apply a texture to the Body.
     */
    public Body(String name, double diameter, double maxAngle, double obliquity, double rotatePeriod, double orbitalPeriod, Body parent, double orbitHeight, double orbitWidth, String textureURL, Color color) {
        this.name = name;
        this.rotatePeriod = rotatePeriod;
        this.orbitalPeriod = orbitalPeriod;
        this.children = new ArrayList<>();
        this.rings = new ArrayList<>();
        this.parent = parent;
        this.orbitWidth = (orbitWidth / 149.6) * SolarSystemCourseWork2.pixelsPerAU;
        this.orbitHeight = (orbitHeight / 149.6) * SolarSystemCourseWork2.pixelsPerAU;
        this.maxAngle = maxAngle;
        currAngle = maxAngle;

        radius = (diameter / SolarSystemCourseWork2.sizeScale) / 2.0;
        planet = StdDraw3D.sphere(0, 0, 0, radius, -90, obliquity, 0, textureURL);

        StdDraw3D.setPenColor(color);
        text = StdDraw3D.text3D(0, 0, 0, name);
        text.scale(5.0);
        text.setPosition(planet.getPosition().x, 0, (planet.getPosition().z + radius + 1));

        if (parent != null) {
            calculateForTime(SolarSystemCourseWork2.timeShiftMilliseconds, SolarSystemCourseWork2.timeAmplifier);

            planet.setPosition(parent.getPosition());
            double[] startCoords = calculateNextPosition(this.orbitWidth, this.maxAngle);
            planet.move(startCoords[0], 0, startCoords[2]);
            originalX = parent.getPosition().x + startCoords[0];
            originalY = 0;

            path = drawDotsPath();
            parent.addChild(this);
        } else {
            planet.setPosition(orbitWidth, orbitHeight, maxAngle);
            path = null;

            calculateRotationDegree(rotatePeriod, SolarSystemCourseWork2.timeShiftMilliseconds, SolarSystemCourseWork2.timeAmplifier);
            SolarSystemCourseWork2.rootBodies.add(this);
        }
        StdDraw3D.setPenColor(Color.WHITE);
    }

    public double getRadius() { return radius; }
    public StdDraw3D.Vector3D getPosition() { return planet.getPosition(); }
    public String getName() { return name; }
    public Body getParent() { return parent; }
    public int getNumChildren() { return children.size(); }

    /**
     * Method that returns the child at the given index, if the given index is valid.
     * @param index int is the index of the child we want to retrieve.
     * @return Body the child Body at the given index, otherwise null if the index does not exist.
     */
    public Body getChild(int index) {
        if ((children.size() > 0) && (index < children.size())) {
            return children.get(index);
        }
        return null;
    }

    /**
     * Helper method that calculates the amount the Body must rotate on its own axis for the current time scale.
     * @param hoursFullRotate double the amount of hours it takes the real-life Body to rotate once around its axis.
     * @param timeShiftMs double is the time between each frame of the animation of the Solar System.
     * @param timeAmplifier double is the amount of time should be amplified with to speed the animation up.
     */
    private void calculateRotationDegree(double hoursFullRotate, double timeShiftMs, double timeAmplifier) {
        double millisecondsFullEquatorRotation = hoursFullRotate * 3600000.0;
        double amountOfShifts = millisecondsFullEquatorRotation / (timeShiftMs * timeAmplifier);
        rotationDegrees = 360.0 / amountOfShifts;
    }

    /**
     * Helper method that calculates the degree at which the Body must rotate around its parent for the current time scale.
     * @param timeShiftMs double is the time between each frame of the animation of the Solar System.
     * @param timeAmplifier double is the amount of time should be amplified with to speed the animation up.
     */
    private void calcAnglePerTimeShift(double timeShiftMs, double timeAmplifier) {
        double millisecondsOP = orbitalPeriod * 86400000.0;
        double amountOfMovement = millisecondsOP / (timeShiftMs * timeAmplifier);
        frameOrbitDegree = 360.0 / amountOfMovement;
    }

    /**
     * Helper method that calculates the degree at which the Body must move up/down for the current time scale.
     * @param timeShiftMs double is the time between each frame of the animation of the Solar System.
     * @param timeAmplifier double is the amount of time should be amplified with to speed the animation up.
     */
    private void calcAngleAltPerTimeShift(double timeShiftMs, double timeAmplifier) {
        double shiftMsOP = (orbitalPeriod * 86400000.0) / (timeShiftMs * timeAmplifier);
        double halfShiftOP = shiftMsOP / 2.0;
        angleAlt = (maxAngle * 2.0) / halfShiftOP;
    }

    /**
     * Helper method that calls the Body and all its children to recalculate its movements for a new time scale set by
     *  the user during the simulation.
     * @param timeShiftMs double is the time between each frame of the animation of the Solar System.
     * @param timeAmplifier double is the amount of time should be amplified with to speed the animation up.
     */
    public void calculateForTime(double timeShiftMs, double timeAmplifier) {
        // Recalculate your own movements for the new time scale.
        calculateRotationDegree(this.rotatePeriod, timeShiftMs, timeAmplifier);
        calcAnglePerTimeShift(timeShiftMs, timeAmplifier);
        calcAngleAltPerTimeShift(timeShiftMs, timeAmplifier);
        // Make your children also recalculate their movements.
        for (Body child : children) {
            child.calculateForTime(timeShiftMs, timeAmplifier);
        }
    }

    /**
     * Method that uses trigonometry to calculate the next x & z positions the Body must be at in its orbit
     *  (if it has a parent) for the current time scale.
     * The y-position is calculated seperately by the Body, as it does not affect the height and angle at which the
     *  planet must be positioned.
     * @param nextX double
     * @param nextAngle double
     * @return double[] the x (and z)
     */
    private double[] calculateNextPosition(double nextX, double nextAngle) {
        if (nextAngle != 0) {
            double powDist = Math.pow(nextX, 2.0);
            double thirdSide = Math.sqrt(powDist + powDist - 2.0 * nextX * nextX * Math.cos(Math.toRadians(nextAngle)));

            double x = Math.sqrt(Math.pow(nextX, 2.0) - Math.pow((0.5 * thirdSide), 2.0));

            double semiPerimeter = (nextX + nextX + thirdSide) / 2.0;
            double triArea = Math.sqrt(semiPerimeter * (semiPerimeter - nextX) * (semiPerimeter - thirdSide) * (semiPerimeter - nextX));
            double z = (2.0 * triArea) / nextX;

            return new double[]{x, 0, z};
        }
        return new double[]{nextX, 0, 0};
    }

    /**
     * Method that moves the Body on its orbit, as well as rotating it on its axis. Also moves any of its rings and its
     *  path. If it has children, it will call these to move too.
     * @param parentMoveX double is the amount the parent has moved on the x-axis. Call as 0 for a Body without a parent.
     * @param parentMoveY double is the amount the parent has moved on the y-axis. Call as 0 for a Body without a parent.
     * @param parentMoveZ double is the amount the parent has moved on the z-axis. Call as 0 for a Body without a parent.
     */
    public void movePlanet(double parentMoveX, double parentMoveY, double parentMoveZ) {
        // Rotate the planet around its own axis.
        planet.rotateRelative(0, rotationDegrees, 0);

        double thisMoveX = 0;
        double thisMoveY = 0;
        double thisMoveZ = 0;
        if (parent != null) {
            // Move this planet first by the amount the parent moved. This ensures we stay on the orbit path.
            planet.move(parentMoveX, parentMoveY, parentMoveZ);

            StdDraw3D.Vector3D parentPos = parent.getPosition();
            // Make the planet move by the actual amount it would move for the user-set time shift.
            if (orbitalPeriod > 0) {
                currOrbitDegree = (currOrbitDegree + frameOrbitDegree) % 360.0;
            } else {
                currOrbitDegree = ((currOrbitDegree - frameOrbitDegree) + 360.0) % 360.0;
            }
            double orbitRad = currOrbitDegree * (Math.PI / 180.0); // Convert to radians

            // If viewing top-down, calculate on what position of the orbit the planet has to be next, including the
            //  current location and the last movement of the parent to get where in the simulation space the Body must be.
            double nextX = orbitWidth * Math.cos(orbitRad) + parent.originalX;
            originalX = nextX;
            double nextY = orbitHeight * Math.sin(orbitRad) + parent.originalY;
            originalY = nextY;

            // Decide if the Body should be moving up or down in angle in our orbit.
            if (currAngle <= -maxAngle) {
                // If the Body has reached the lowest point in its orbit, then make it move up by making the angleAlt negative.
                downwards = false;
                angleAlt = -angleAlt;
            } else if ( currAngle >= maxAngle) {
                // If the Body has reached the highest point in its orbit, then make it move down by making the angleAlt positive.
                downwards = true;
                angleAlt = -angleAlt;
            }

            // To prevent rounding done by Java at the wrong moments, we round at a increase smaller than a certain amount.
            if (Math.abs(angleAlt) < 0.1) { angleAlt = Math.round(angleAlt); }
            currAngle = currAngle + angleAlt;

            // Take the top-down location calculation and adapt the x position with trigonometry to get the actual x
            //  the Body has to be at, as well as its z height. Values have to always be positive.
            double[] nextCoords = calculateNextPosition(Math.abs(nextX - parent.originalX), Math.abs(currAngle));
            if ((downwards && (nextX - parent.originalX) <= 0) || (!downwards && (nextX - parent.originalX) <= 0)) {
                // If we are in the negative (lower) half of the orbit, then make the calculated x & z positions negative,
                //  since we had to calculate them with positive values only.
                nextCoords[0] = -nextCoords[0];
                nextCoords[2] = -nextCoords[2];
            }

            // Get a delta of the next coordinates compared to the current coordinates to decide how much the Body
            //  should move from its current position.
            thisMoveX = (nextCoords[0] + parent.originalX) - planet.getPosition().x;
            thisMoveY = nextY - planet.getPosition().y;
            thisMoveZ = (nextCoords[2] + parentPos.z) - planet.getPosition().z;

            // Move the planet according to the amount we calculated.
            planet.move(thisMoveX, thisMoveY, thisMoveZ);

            // Set the position of the visuals of this planet's path to be centered around the parent again, as it moved before us.
            path.setPosition(parentPos.x, parentPos.y, parentPos.z);
            text.setPosition(planet.getPosition().x, planet.getPosition().y, (planet.getPosition().z + radius + 1));

            // If the planet has rings, then these should be moved just like the planet itself.
            if (rings.size() > 0) {
                for (StdDraw3D.Shape ring : rings) {
                    ring.move(parentMoveX, parentMoveY, parentMoveZ);
                    ring.move(thisMoveX, thisMoveY, thisMoveZ);
                }
            }
        }

        // Then move every child this planet has as well.
        if (children.size() > 0) {
            for (Body child : children) {
                child.movePlanet(thisMoveX, thisMoveY, thisMoveZ);
            }
        }
    }

    /**
     * Method that draws a dotted circle/ellipses showing the orbit path of the Body at all times. The amount of dots
     *  drawn is derived from the biggest distance the Body has from its parent.
     * @return StdDraw3D.Shape is the points of the path combined as one shape.
     */
    private StdDraw3D.Shape drawDotsPath() {
        // Calculate by the size of the path radius how many dots we want to draw. This is decided from the biggest
        //  distance the Body will have from its parent.
        int amountOfPoints = (int)(Math.round(SolarSystemCourseWork2.orbitDots * Math.max(orbitHeight, orbitWidth)));

        StdDraw3D.Shape[] pathPoints = new StdDraw3D.Shape[amountOfPoints];
        for (int i = 1; i <= amountOfPoints; i++) {
            double angleRad = i * (Math.PI / (amountOfPoints / 2.0)); // Convert angle to radians
            // Then for every point in numberOfPoints, calculate the X and Y values for each of the points.
            double pointX = orbitWidth * Math.cos(angleRad);
            double pointY = orbitHeight * Math.sin(angleRad);

            pathPoints[i - 1] = StdDraw3D.point(pointX, pointY, 0);
        }

        // Combine all the dots together to get one shape: A circle of dots.
        StdDraw3D.Shape path = StdDraw3D.combine(pathPoints);
        // Now set the center of the circle of dots to be the center of the parent planet.
        StdDraw3D.Vector3D parentPosition = parent.getPosition();
        path.setPosition(parentPosition.x, parentPosition.y, parentPosition.z);

        // If the planet orbits at an angle around the parent, the path should show this.
        if (maxAngle != 0) { path.rotate(0, -maxAngle, 0); }

        return path;
    }

    private void addChild(Body child) {
        children.add(child);
    }

    public void addRing(double radius, double angle, Color color) {
        Color originalColor = StdDraw3D.getPenColor();
        StdDraw3D.setPenColor(color);
        StdDraw3D.Vector3D planetPos = planet.getPosition();
        rings.add(StdDraw3D.cylinder(planetPos.x, planetPos.y, planetPos.z, (radius / SolarSystemCourseWork2.sizeScale), 0, -90, (angle % 180.0), 0));
        StdDraw3D.setPenColor(originalColor);
    }

    public void addRing(double radius, double angle, String imageURL) {
        StdDraw3D.Vector3D planetPos = planet.getPosition();
        rings.add(StdDraw3D.ellipsoidSP(planetPos.x, planetPos.y, planetPos.z, (radius / SolarSystemCourseWork2.sizeScale), 0, (radius / SolarSystemCourseWork2.sizeScale), 90, (angle % 180.0), 0, imageURL));
    }
}
