import java.awt.*;

public class KeplerSolarSystemTest {
    // The variable below dictates how many milliseconds are between each frame of movement.
    //  The higher the number, the longer it will take before the next frame is shown.
    public static final int timeShiftMilliseconds = 1;
    // The variable below dictates how many milliseconds are performed per every actual shift.
    //  So for instance 1:100 would be 100 milliseconds of movement per every real-time 1 millisecond shift.
    public static int timeAmplifier = 100000;
    // The amount of units within the scale, generally pixels, 1 AU (~149.6 * 10^6 km) spans in the simulation.
    public static final double pixelsPerAU = 20;

    public static void main(String[] args) {
        StdDraw3D.setCanvasSize(900, 900);
        StdDraw3D.setScale(-100.0, 100.0);
        StdDraw3D.clearLight();

        new KeplerSolarSystemTest().run();
    }

    private void run() {
        StdDraw3D.setBackgroundSphere("./assets/textures/2k_stars.jpg");
        StdDraw3D.pointLight(0, 0, 0, Color.WHITE);

        // DEBUG: Draw the eccliptic plane to check if orbit angles are positioned correctly.
        StdDraw3D.setPenColor(Color.RED);
//        StdDraw3D.cylinder(0, 0, 0, 100, .05, 90, 0, 0);
        StdDraw3D.setPenColor(Color.WHITE);

        KeplerBodyTest sun = new KeplerBodyTest(1391000, 576, 0, null, 0, 0, 0, 0, 0, 0,"./assets/textures/2k_sun.jpg");

        KeplerBodyTest mercury = new KeplerBodyTest(1000000, 1407.6, 0.034, sun, 46.0, 0.205, 29.125, 48.331, 7.005, 174.795, "./assets/textures/2k_mercury.jpg");
//        KeplerBodyTest venus = new KeplerBodyTest(3, 3.4, 177.4, -5832.5, 224.7, sun, 7, "./assets/textures/2k_venus_atmosphere.jpg");
//
        KeplerBodyTest earth = new KeplerBodyTest(1000000, 23.9, 23.4, sun, 147.1, 0.017, 288.064, 174.873, 0.0, 357.529, "./assets/textures/2k_earth_daymap.jpg");
        KeplerBodyTest moon = new KeplerBodyTest(500000, 655.7, 6.7, earth, 0.363, 0.055, 318.150, 258.372, 5.1, 134.963, "./assets/textures/2k_moon.jpg");
//
//        KeplerBodyTest mars = new KeplerBodyTest(1.5, 1.9, 25.2, 24.6, 687.0, sun, 15, "./assets/textures/2k_mars.jpg");
        KeplerBodyTest jupiter = new KeplerBodyTest(1000000, 9.9, 3.1, sun, 740.5, 0.04849, 273.867, 100.464, 1.303, 20.020, "./assets/textures/2k_jupiter.jpg");
//        KeplerBodyTest saturn = new KeplerBodyTest(28, 2.5, 26.7, 10.7, 10747, sun, 95, "./assets/textures/2k_saturn.jpg");
//        KeplerBodyTest uranus = new KeplerBodyTest(12, .8, 97.8, -17.2, 30689, sun, 190, "./assets/textures/2k_uranus.jpg");
//        KeplerBodyTest neptune = new KeplerBodyTest(11.5, 1.8, 28.3, 16.1, 59800, sun, 300, "./assets/textures/2k_neptune.jpg");
//        KeplerBodyTest pluto = new KeplerBodyTest(0.4, 17.2, 122.5, -153.3, 90560, sun, 450, "./assets/textures/2k_pluto.jpg");

        while (true) {
            sun.movePlanet();

            StdDraw3D.show(timeShiftMilliseconds);
        }
    }
}
