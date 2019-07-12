import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Application file that runs the correct submission Solar System made using StdDraw3D & Java3D. Requires the included
 * StdDraw3D file to run, due to added functions in that over the original.
 * This APL draws a simulation of our Solar System at a reduced scale but with the planets moving in real time (although not on the correct positions compared to real life).
 *
 * @author Yoran Kerbusch (EHU Student 24143341)
 * Made for CIS2166 Computer Graphics & Modelling Coursework 2 (2018-2019)
 */
public class SolarSystemCourseWork2 {
    public static final ArrayList<Body> rootBodies = new ArrayList<>();

    // The variable below dictates how many milliseconds are between each frame of movement.
    //  The higher the number, the longer it will take before the next frame is shown.
    public static final int timeShiftMilliseconds = 1;
    // The variable below dictates how many milliseconds are performed per every actual shift.
    //  So for instance 1:100 would be 100 milliseconds of movement per every real-time 1 millisecond shift.
    public static int timeAmplifier = 1;
    private static int increaseAmount = 1;
    private static boolean timeUpdated = true;

    // Scaling options for the planets and the camDist between them.
    public static final double pixelsPerAU = 50.0;
    public static final double sizeScale = 4000.0;

    // Performance settings.
    public static double orbitDots = 2;

    // Variables for camera control.
    private static boolean freeMode = true;
    private static double camDist = 5;
    private static Body currentOrbit;
    private static HashMap<Integer, Boolean> pressedKeys = new HashMap<>();
    private static int childPointer = 0;

    public static void main(String[] args) {
        // Lower these variables if you're experiencing FPS drops.
        // DEFAULT_BACK_CLIP is how much camDist it takes from the camera for StdDraw3D to stop drawing objects at that camDist.
        StdDraw3D.DEFAULT_BACK_CLIP = 25;
        // orbitDots is the amplifier for the amount of dots drawn for the orbit line of a planet. Lowering this means
        //  less dots, thus making the simulation smoother due to less objects being drawn.
        orbitDots = 2.5;

        StdDraw3D.fullscreen();
        StdDraw3D.setScale(-100.0, 100.0);
        StdDraw3D.clearLight();

        new SolarSystemCourseWork2().run();
    }

    private void run() {
        // Add all the used keys to the hashmap, so we can keep track of if they're still pressed. This makes it so that
        //  if a key is pressed/held, it only does the action once until the key is released and pressed again.
        pressedKeys.put(KeyEvent.VK_F, false);
        pressedKeys.put(KeyEvent.VK_D, false);
        pressedKeys.put(KeyEvent.VK_A, false);
        pressedKeys.put(KeyEvent.VK_W, false);
        pressedKeys.put(KeyEvent.VK_S, false);
        pressedKeys.put(KeyEvent.VK_C, false);
        pressedKeys.put(KeyEvent.VK_Z, false);
        pressedKeys.put(KeyEvent.VK_E, false);
        pressedKeys.put(KeyEvent.VK_Q, false);
        pressedKeys.put(KeyEvent.VK_UP, false);
        pressedKeys.put(KeyEvent.VK_DOWN, false);
        pressedKeys.put(KeyEvent.VK_RIGHT, false);
        pressedKeys.put(KeyEvent.VK_LEFT, false);

        // Sets a starry night as the background for the simulation.
        StdDraw3D.setBackgroundSphere("./assets/textures/2k_stars.jpg");
        // Give a slight ambient light so the dark sides of the planets aren't too dark.
        StdDraw3D.ambientLight(new Color(90, 90, 90));
        // Draw the light coming from the sun.
        StdDraw3D.pointLight(0, 0, 0, Color.WHITE);

        // Create all the planets. The sun doesn't rotate around anything, so it has no parent.
        Body sun = new Body("Sun", (1391000 / 15.0), 0, 0, 576, 0, null, 0, 0, "./assets/textures/2k_sun.jpg", Color.ORANGE);

        Body mercury = new Body("Mercury", 4879, 7, .034, 1407.6, 88, sun, 46, 69.8, "./assets/textures/2k_mercury.jpg", Color.GRAY);
        Body venus = new Body("Venus", 12104, 3.4, 177.4, -5832.5, 224.7, sun, 107.5, 108.9, "./assets/textures/2k_venus_atmosphere.jpg", Color.ORANGE);

        Body earth = new Body("Earth", 12756, 0, 23.4, 23.9, 365.2, sun, 147.1, 152.1, "./assets/textures/2k_earth_daymap.jpg", Color.BLUE);
        Body moon = new Body("Moon", 3475, 5.1, 6.7, 655.7, 27.3, earth, (.363 * 20.0), (.406 * 20.0), "./assets/textures/2k_moon.jpg", Color.WHITE);
        currentOrbit = earth;
        int planetPointer = 2;

        Body mars = new Body("Mars", 6792, 1.9, 25.2, 24.6, 687.0, sun, 206.6, 249.2, "./assets/textures/2k_mars.jpg", Color.RED);

        Body jupiter = new Body("Jupiter", 142984, 1.3, 3.1, 9.9, 4331, sun, (740.5 / 1.75), (816.6 / 1.75), "./assets/textures/2k_jupiter.jpg", Color.YELLOW);
        Body io = new Body("Io", 3643, .04, 0, 42.5, 1.8, jupiter, (.420 * 145.0), (.424 * 145.0), "./assets/textures/io.jpg", Color.WHITE);
//        Body europa = new Body("Europa", 3122, .47, 0, 85.2, 3.6, jupiter, (.664 * 110.0), (.678 * 110.0), "./assets/textures/europa.jpg", Color.YELLOW);
        Body ganymede = new Body("Ganymede", 5262, .18, 0, 171.7, 7.2, jupiter, (1.068 * 85.0), (1.072 * 85.0), "./assets/textures/ganymede.jpg", Color.LIGHT_GRAY);
        Body callisto = new Body("Callisto", 4821, .19, 0, 400.5, 16.7, jupiter, (1.87 * 60.0), (1.896 * 60.0), "./assets/textures/callisto.jpg", Color.ORANGE);

        Body saturn = new Body("Saturn", 120536, 2.5, 26.7, 10.7, 10747, sun, (1352.6 / 1.75), (1514.5 / 1.75), "./assets/textures/2k_saturn.jpg", Color.WHITE);
        saturn.addRing(180000, 26.7, "./assets/textures/2k_saturn_ring_alpha.png");
        Body titan = new Body("Titan", 5150, .33, 0, 382.69, 15.945, saturn, (1.222 * 125.0), (1.187 * 125.0), "./assets/textures/titan.jpg", Color.GRAY);

        Body uranus = new Body("Uranus", 51118, .8, 97.8, -17.2, 30689, sun, (2741.3 / 1.75), (3003.6 / 1.75), "./assets/textures/2k_uranus.jpg", Color.CYAN);
        Body titania = new Body("Titania", 1577.8, .08, 0, 208.941, 8.705, uranus, (.436 * 55.0), (.436 * 55.0), Color.YELLOW);
        Body oberon = new Body("Oberon", 1522.8, .07, 0, 323.118, 13.463, uranus, (.584 * 60.0), (.583 * 60.0), Color.GRAY);

        Body neptune = new Body("Neptune", 49528, 1.8, 28.3, 16.1, 59800, sun, (4444.5 / 1.75), (4545.7 / 1.75), "./assets/textures/2k_neptune.jpg", Color.BLUE);
        Body pluto = new Body("Pluto", 2370, 17.2, 122.5, -153.3, 90560, sun, (4436.8 / 1.75), (7375.9 / 1.75), "./assets/textures/plutomap2k.jpg", Color.LIGHT_GRAY);

        // Set how much the user scrolls per unit of movement of the scroll wheel to be half the normal amount.
        double originalZoom = StdDraw3D.getZoomScale() / 2.0;
        StdDraw3D.setOrbitScale(originalZoom);

        while (true) {
            StdDraw3D.clearOverlay();

            // Move all the planets that do not have parents.
            for (Body body : rootBodies) {
                // Move all the planet. If it has children, it will make sure that they move (and the children will do this for their children, etc).
                body.movePlanet(0, 0, 0);
            }

            if (!freeMode) {
                // If we are locked on a planet to orbit it, keep the camera to it.
                StdDraw3D.Vector3D currentPos = currentOrbit.getPosition();
                StdDraw3D.setOrbitCenter(currentPos);
                StdDraw3D.setCameraPosition(currentPos.x, currentPos.y, (currentPos.z + currentOrbit.getRadius() + camDist));
            }

            /*-----Keys moving the camera when not in free-mode-------------------------------------------------------*/
            if (StdDraw3D.isKeyPressed(KeyEvent.VK_F) && !pressedKeys.get(KeyEvent.VK_F)) {
                // F - Free-mode. Allows the user to break the stuck orbit to planets and fly free in orbit mode around.
                pressedKeys.put(KeyEvent.VK_F, true);
                freeMode = !freeMode;

                if (!freeMode) {
                    camDist = 5;
                    // If we entered stuck orbit mode, move the camera to the current planet selected.
                    repositionCamera();
                    // Also decrease the zoom further so the user can see the planet better.
                    StdDraw3D.setOrbitScale(originalZoom / 4.0);
                } else {
                    // If we entered free-mode, then set the orbit scale to the normal amount.
                    StdDraw3D.setOrbitScale(originalZoom);
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_D) && !pressedKeys.get(KeyEvent.VK_D)) {
                // D - Next child planet. Goes to the next planet, if the current parent has any.
                pressedKeys.put(KeyEvent.VK_D, true);
                camDist = 5;
                if (freeMode) {
                    // If the user is in free-mode, then this acts like the F-key, switching them to stuck orbit mode,
                    //  returning them to the planet they were orbiting before they entered free-mode.
                    freeMode = false;
                    repositionCamera();
                    StdDraw3D.setOrbitScale(originalZoom / 2.0);
                } else {
                    // If the user is not in free-mode, then this should center the user to the next child planet.
                    Body parent = currentOrbit.getParent();
                    if (parent != null) {
                        // Loop through the children forwards, going to the first child if we are going forwards from the last child.
                        planetPointer = (planetPointer + 1) % parent.getNumChildren();
                        currentOrbit = parent.getChild(planetPointer);
                    } else {
                        planetPointer = 0;
                    }
                    repositionCamera();
                    childPointer = 0;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_A) && !pressedKeys.get(KeyEvent.VK_A)) {
                // A - Previous child planet. Goes to the previous planet, if the current parent has any.
                pressedKeys.put(KeyEvent.VK_A, true);
                camDist = 5;
                if (freeMode) {
                    freeMode = false;
                    repositionCamera();
                    StdDraw3D.setOrbitScale(originalZoom / 2.0);
                } else {
                    // If the user is not in free-mode, then this should center the user to the previous child planet.
                    Body parent = currentOrbit.getParent();
                    if (parent != null) {
                        // Loop through the children backwards, going to the last child if we are going backwards from the first child.
                        planetPointer = ((planetPointer - 1) + parent.getNumChildren()) % parent.getNumChildren();
                        currentOrbit = parent.getChild(planetPointer);
                    } else {
                        planetPointer = 0;
                    }
                    repositionCamera();
                    childPointer = 0;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_W) && !pressedKeys.get(KeyEvent.VK_W)) {
                // W - Go to current selected child of currently orbiting planet, making that child the new orbit center for the camera.
                pressedKeys.put(KeyEvent.VK_W, true);
                camDist = 5;
                if (currentOrbit.getNumChildren() > 0) {
                    // Of course, only go to the child if the parent has any children.
                    currentOrbit = currentOrbit.getChild(childPointer);
                    repositionCamera();
                    childPointer = 0;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_S) && !pressedKeys.get(KeyEvent.VK_S)) {
                // S - Go to the parent of currently orbiting planet, making that parent the new orbit center for the camera.
                pressedKeys.put(KeyEvent.VK_S, true);
                camDist = 5;
                if (currentOrbit.getParent() != null) {
                    // When the user presses "S", go a step back to the parent of the current planet they are looking at.
                    currentOrbit = currentOrbit.getParent();
                    repositionCamera();
                } else {
                    // You cannot go further back the parent line than the sun, since it's the parent of all planets here.
                }
            }

            if (StdDraw3D.isKeyPressed(KeyEvent.VK_E) && !pressedKeys.get(KeyEvent.VK_E)) {
                // E - If the current orbited planet has children, then select the next child they have, if there is more than one.
                //  This allows the user to select a child before pressing W, instead of pressing W and having to use A/D.
                pressedKeys.put(KeyEvent.VK_E, true);
                int numChildren = currentOrbit.getNumChildren();
                if (numChildren > 0) {
                    childPointer = (childPointer + 1) % numChildren;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_Q) && !pressedKeys.get(KeyEvent.VK_Q)) {
                // Q - If the current orbited planet has children, then select the previous child they have, if there is more than one.
                pressedKeys.put(KeyEvent.VK_Q, true);
                int numChildren = currentOrbit.getNumChildren();
                if (numChildren > 0) {
                    childPointer = ((childPointer - 1) + numChildren) % numChildren;
                }
            }

            if (StdDraw3D.isKeyPressed(KeyEvent.VK_C) && !freeMode && !pressedKeys.get(KeyEvent.VK_C)) {
                // C - When not in free-mode, this will zoom the camera away from the planet the user is locked to.
                pressedKeys.put(KeyEvent.VK_C, true);
                camDist++;
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_Z) && !freeMode && !pressedKeys.get(KeyEvent.VK_Z)) {
                // Z - When not in free-mode, this will zoom the camera towards the planet the user is locked to.
                pressedKeys.put(KeyEvent.VK_Z, true);
                camDist--;
            }

            /*-----Keys for changing the time scale-------------------------------------------------------------------*/
            if (StdDraw3D.isKeyPressed(KeyEvent.VK_UP) && !pressedKeys.get(KeyEvent.VK_UP)) {
                // Up arrow - Increases the time amplifier by the set increase amount (set by the left/right arrow).
                //  Makes the simulation move that amount of milliseconds per real-life millisecond, to a max of 1:1000000.
                pressedKeys.put(KeyEvent.VK_UP, true);
                if (timeAmplifier < 1000000) {
                    timeAmplifier = ((timeAmplifier + increaseAmount) > 1000000) ? 1000000 : (timeAmplifier + increaseAmount);
                    timeUpdated = false;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_DOWN) && !pressedKeys.get(KeyEvent.VK_DOWN)) {
                // Down arrow - Decreases the time amplifier by the set increase amount (set by the left/right arrow).
                //  Decrease fully to pause the simulation (1:0).
                pressedKeys.put(KeyEvent.VK_DOWN, true);
                if (timeAmplifier > 0) {
                    timeAmplifier = ((timeAmplifier - increaseAmount) < 0) ? 0 : (timeAmplifier - increaseAmount);
                    timeUpdated = false;
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_RIGHT) && !pressedKeys.get(KeyEvent.VK_RIGHT)) {
                // Right arrow - Increases the increase amount the timer will be in-/decreased by when pressing the up or down arrow.
                //  Increases by *10 every time, from min 1 to max 100000.
                pressedKeys.put(KeyEvent.VK_RIGHT, true);
                if (increaseAmount < 100000) {
                    increaseAmount = ((increaseAmount * 10) > 100000) ? 100000 : (increaseAmount * 10);
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_LEFT) && !pressedKeys.get(KeyEvent.VK_LEFT)) {
                // Left arrow - Decreases the increase amount the timer will be in-/decreased by when pressing the up or down arrow.
                pressedKeys.put(KeyEvent.VK_LEFT, true);
                if (increaseAmount > 1) {
                    increaseAmount = ((increaseAmount / 10) < 1) ? 1 : (increaseAmount / 10);
                }
            } else if (StdDraw3D.isKeyPressed(KeyEvent.VK_ENTER)) {
                // Enter key - Sets the current timescale to the new one, if the user pressed the up or down arrow.
                //  This has to be pressed to have the new timescale be used and does not work if nothing was changed.
                if (!timeUpdated) {
                    for (Body body : rootBodies) {
                        body.calculateForTime(timeShiftMilliseconds, timeAmplifier);
                    }
                    timeUpdated = true;
                }
            }

            // Check if any of the keys have been released and if so, record that so we know they can be pressed again.
            for (HashMap.Entry<Integer, Boolean> pair : pressedKeys.entrySet()) {
                if (!StdDraw3D.isKeyPressed(pair.getKey())) {
                    pair.setValue(false);
                }
            }

            // Show GUI information so the user knows what they are changing and setting.
            StdDraw3D.setPenColor(Color.WHITE);
            StdDraw3D.overlayText(10, 95, "[F] Free-flight?: " + freeMode);
            StdDraw3D.overlayText(10, 89, "[A/D] Current orbit: " + currentOrbit.getName());
            if (currentOrbit.getParent() != null) {
                StdDraw3D.overlayText(10, 83, "[S] Parent: " + currentOrbit.getParent().getName());
            } else {
                StdDraw3D.overlayText(10, 83, "[S] Parent: No parent");
            }
            if (currentOrbit.getChild(0) != null) {
                StdDraw3D.overlayText(10, 77, "[W (Q/E)] Selected Child: " + currentOrbit.getChild(childPointer).getName() + " (" + (childPointer + 1) + "/" + currentOrbit.getNumChildren() + ")");
            } else {
                StdDraw3D.overlayText(10, 77, "[W (Q/E)] First Child: No child planets");
            }

            StdDraw3D.overlayText(70, 95, "Base speed (ms): " + timeShiftMilliseconds);
            StdDraw3D.overlayText(70, 89, "[\u2191/\u2193] Amplifier (ms): x" + timeAmplifier);
            StdDraw3D.overlayText(70, 83, "[\u2190/\u2192] Increase: +" + increaseAmount);
            StdDraw3D.overlayText(70, 77, "[Enter] Updated?: " + timeUpdated);

            // Normally, there is a millisecond between each drawn frame, so that the simulation follows real-time.
            StdDraw3D.show(timeShiftMilliseconds);
        }
    }

    /**
     * Repositions the camera to be centered to the currently orbited planet, for the locked camera orbit mode.
     */
    private void repositionCamera() {
        StdDraw3D.Vector3D currentPos = currentOrbit.getPosition();
        // This moves the camera slightly away from the selected planet, so the camera always starts at the same place.
        StdDraw3D.setCameraPosition(currentPos.x, currentPos.y, (currentPos.z + currentOrbit.getRadius() + camDist));
        StdDraw3D.setCameraOrientation(0, 0, 0);
        // This makes it so that the user's camera moves around the planet that is selected.
        StdDraw3D.setOrbitCenter(currentPos.x, currentPos.y, currentPos.z);
    }
}
