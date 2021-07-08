package pouryapb;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirConditioner {

	public static final int COOLER_AND_HEATER_OFF = 0;
	public static final int COOLER_ON_HEATER_OFF = 1;
	public static final int COOLER_OFF_HEATER_ON = 2;
	public static final int OUT = 3;
	public static final int COOLER_LEVEL_1 = 0;
	public static final int COOLER_LEVEL_2 = 1;
	public static final int COOLER_LEVEL_3 = 2;
	public static final int HEATER_LOW = 0;
	public static final int HEATER_HIGH = 1;
	public static final Scanner sc = new Scanner(System.in);

	public static final String RESET = "\033[0m"; // Text Reset
	public static final String RED = "\033[0;31m"; // RED
	public static final String GREEN = "\033[0;32m"; // GREEN
	public static final String YELLOW = "\033[0;33m"; // YELLOW
	public static final String CYAN = "\033[0;36m"; // CYAN

	private static final String HEATER = "Heater";
	private static final String COOLER = "Cooler";

	private static final Logger LOGGER = Logger.getLogger(AirConditioner.class.getName());

	public static void main(String[] args) {
		int pState = COOLER_AND_HEATER_OFF;
		int nState = Integer.MAX_VALUE;
		int coolerPState;
		int coolerNState = Integer.MAX_VALUE;
		int heaterPState;
		int heaterNState = Integer.MAX_VALUE;
		float event = -1;
		var exit = false;

		while (!exit) {
			switch (pState) {
				case COOLER_AND_HEATER_OFF:
					turnOff(HEATER);
					turnOff(COOLER);
					event = waitForEvent(15, 35);
					if (event < 15)
						nState = COOLER_OFF_HEATER_ON;
					else if (event > 35)
						nState = COOLER_ON_HEATER_OFF;
					break;
				case COOLER_ON_HEATER_OFF:
					turnOn(COOLER);
					turnOff(HEATER);
					coolerPState = COOLER_LEVEL_1;
					while (coolerPState != OUT) {
						switch (coolerPState) {
							case COOLER_LEVEL_1:
								crs(4);
								event = waitForEvent(25, 40);
								if (event < 25)
									coolerNState = OUT;
								else if (event > 40)
									coolerNState = COOLER_LEVEL_2;
								break;
							case COOLER_LEVEL_2:
								crs(6);
								event = waitForEvent(35, 45);
								if (event < 35)
									coolerNState = COOLER_LEVEL_1;
								else if (event > 45)
									coolerNState = COOLER_LEVEL_3;
								else
									coolerNState = coolerPState;
								break;
							case COOLER_LEVEL_3:
								crs(8);
								event = waitForEvent(40, Integer.MAX_VALUE);
								if (event < 40)
									coolerNState = COOLER_LEVEL_2;
								break;
							default:
								exit = true;
						}
						coolerPState = coolerNState;
					}
					if (event < 25)
						nState = COOLER_AND_HEATER_OFF;
					break;
				case COOLER_OFF_HEATER_ON:
					turnOff(COOLER);
					turnOn(HEATER);
					heaterPState = HEATER_LOW;
					while (heaterPState != OUT) {
						switch (heaterPState) {
							case HEATER_LOW:
								hm(HEATER_LOW);
								event = waitForEvent(10, 30);
								if (event > 30)
									heaterNState = OUT;
								else if (event < 10)
									heaterNState = HEATER_HIGH;
								break;
							case HEATER_HIGH:
								hm(HEATER_HIGH);
								event = waitForEvent(Integer.MIN_VALUE, 15);
								if (event > 15)
									heaterNState = HEATER_LOW;
								break;
							default:
								exit = true;
						}
						heaterPState = heaterNState;
					}
					if (event > 30)
						nState = COOLER_AND_HEATER_OFF;
					break;
				default:
					exit = true;
			}
			pState = nState;
		}
	}

	public static void turnOff(String device) {
		LOGGER.log(Level.INFO, () -> RED + device + " turned off!" + RESET);
	}

	public static void turnOn(String device) {
		LOGGER.log(Level.INFO, () -> GREEN + device + " turned on!" + RESET);
	}

	public static float waitForEvent(int begin, int end) {
		LOGGER.log(Level.INFO, () -> YELLOW + "What's the temperature now?" + RESET);
		var event = Float.parseFloat(sc.next());
		while (event >= begin && event <= end) {
			LOGGER.log(Level.INFO, () -> YELLOW + "Nothing changed, what's the temperature now?" + RESET);
			event = Float.parseFloat(sc.next());
		}
		return event;
	}

	public static void crs(int speed) {
		LOGGER.log(Level.INFO, () -> CYAN + "Cooler rotation speed is set to " + speed + "!" + RESET);
	}

	public static void hm(int mode) {
		LOGGER.log(Level.INFO, () -> CYAN + "Heater mode set to " + (mode == HEATER_LOW ? "low!" : "high!") + RESET);
	}

}
