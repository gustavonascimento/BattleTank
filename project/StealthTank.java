package project;
public class StealthTank extends SolidObject {
	private Polygon3D body[];

	private Polygon3D bodyInvisible[];

	private int bodyAngle;

	private Vector bodyCenter;

	private Polygon3D turret[];

	private Polygon3D turretInvisible[];

	private Polygon3D shadowBody;

	private Polygon3D shadowTurret;

	private Vector turretCenter;

	private int turretAngle;

	private boolean forward, aimRight, aimLeft, firing;

	private int coolDown = 75;

	private int currentCoolDown = 0;

	private Vector displacement = new Vector(0, 0, 0);

	private int bodyAngleDelta;

	private int turretAngleDelta;

	private int position;

	private boolean isVisiblePreviousFrame;

	private double distance;

	private int targetAngle;

	private int targetAngleBody;

	private int previousTargetAngleBody;

	private Vector tempVector1 = new Vector(0, 0, 0);
	private Vector tempVector2 = new Vector(0, 0, 0);

	public boolean active = true;

	private boolean engaged;

	private boolean clearToShoot;

	private int countDownToDeath;

	private int stuckCount;

	private Smoke Smoke;

	private int randomNumber1, randomNumber2;

	private int t = 30;

	public StealthTank(double x, double y, double z, int angle) {
		start = new Vector(x, y, z);
		xDirection = new Vector(1, 0, 0);
		yDirection = new Vector(0, 1, 0);
		zDirection = new Vector(0, 0, 1);

		xDirection.rotate_XZ(angle);
		zDirection.rotate_XZ(angle);

		modelType = 2;
		makeBoundary(0.1, 0.25, 0.1);

		boundary2D = new Rectangle2D(x - 0.1, z + 0.1, 0.2, 0.2);
		position = (int) (x * 4) + (129 - (int) (z * 4)) * 80;
		ObstacleMap.registerObstacle2(this, position);

		findCenter();

		bodyCenter = centre;
		bodyAngle = angle;
		turretAngle = angle;

		randomNumber1 = GameData.getRandom();

		makeBody();
		makeTurret();

		HP = 16;

		lifeSpan = 1;
	}

	private void makeBody() {
		start = bodyCenter.myClone();
		xDirection = new Vector(1.1, 0, 0);
		yDirection = new Vector(0, 1, 0);
		zDirection = new Vector(0, 0, 1.1);

		xDirection.rotate_XZ(bodyAngle);
		zDirection.rotate_XZ(bodyAngle);

		Vector v[];
		body = new Polygon3D[43];
		bodyInvisible = new Polygon3D[43];

		v = new Vector[] { put(-0.04, 0.03, 0.07), put(-0.04, 0.055, 0.04),
				put(-0.04, 0.055, -0.05), put(-0.04, 0.03, -0.07),
				put(-0.04, 0, -0.07), put(-0.04, 0, 0.07) };
		body[0] = new Polygon3D(v, put(-0.04, 0.055, 0.07), put(-0.04, 0.055,
				-0.07), put(-0.04, 0.01, 0.07), Main.textures[35], 1, 0.2, 6);

		v = new Vector[] { put(0.04, 0, 0.07), put(0.04, 0, -0.07),
				put(0.04, 0.03, -0.07), put(0.04, 0.055, -0.05),
				put(0.04, 0.055, 0.04), put(0.04, 0.03, 0.07) };
		body[1] = new Polygon3D(v, put(0.04, 0.055, 0.07), put(0.04, 0.055,
				-0.07), put(0.04, 0.01, 0.07), Main.textures[35], 1, 0.2, 6);

		v = new Vector[] { put(-0.04, 0.03, 0.07), put(0.04, 0.03, 0.07),
				put(0.04, 0.055, 0.04), put(-0.04, 0.055, 0.04) };
		body[2] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 1, 0.3,
				6);

		v = new Vector[] { put(-0.04, 0.055, 0.04), put(0.04, 0.055, 0.04),
				put(0.04, 0.055, -0.05), put(-0.04, 0.055, -0.05) };
		body[3] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 1, 1, 6);

		v = new Vector[] { put(-0.04, 0.055, -0.05), put(0.04, 0.055, -0.05),
				put(0.04, 0.03, -0.07), put(-0.04, 0.03, -0.07) };
		body[4] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 1, 0.3,
				6);

		v = new Vector[] { put(0.04, 0.03, 0.07), put(-0.04, 0.03, 0.07),
				put(-0.04, 0, 0.07), put(0.04, 0, 0.07) };
		body[5] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 1, 0.3,
				6);

		v = new Vector[] { put(-0.04, 0.03, -0.07), put(0.04, 0.03, -0.07),
				put(0.04, 0., -0.07), put(-0.04, 0., -0.07) };
		body[6] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 1, 0.3,
				6);

		tempVector1.set(start);
		start = put(0, 0, 0.01);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.04, 0.03, 0.1),
				put(-0.04, 0.03, 0.03), put(-0.065, 0.03, 0.03) };
		body[7] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.03, 0.1), put(-0.065, 0.03, 0.1),
				put(-0.065, 0.01, 0.11), put(-0.04, 0.01, 0.11) };
		body[8] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.03), put(-0.04, 0.03, 0.03),
				put(-0.04, 0.01, 0.029), put(-0.065, 0.01, 0.029) };
		body[9] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.065, 0.03, 0.03),
				put(-0.065, 0.01, 0.029), put(-0.065, 0.01, 0.11) };
		body[10] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.01, 0.11), put(-0.04, 0.01, 0.029),
				put(-0.04, 0.03, 0.03), put(-0.04, 0.03, 0.1) };
		body[11] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, 0.01, 0.029),
				put(-0.065, -0.01, 0.031), put(-0.065, -0.01, 0.1) };
		body[12] = new Polygon3D(v, put(-0.065, 0.03, 0.11), put(-0.065, 0.03,
				0.029), put(-0.065, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.04, -0.01, 0.1), put(-0.04, -0.01, 0.031),
				put(-0.04, 0.01, 0.029), put(-0.04, 0.01, 0.11) };
		body[13] = new Polygon3D(v, put(-0.04, 0.03, 0.11), put(-0.04, 0.03,
				0.029), put(-0.04, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, -0.01, 0.1),
				put(-0.04, -0.01, 0.1), put(-0.04, 0.01, 0.11) };
		body[14] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, -0.01, 0.031), put(-0.065, 0.01, 0.029),
				put(-0.04, 0.01, 0.029), put(-0.04, -0.01, 0.031) };
		body[15] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		start.set(tempVector1);
		start = put(0, 0, -0.12);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.04, 0.03, 0.1),
				put(-0.04, 0.03, 0.03), put(-0.065, 0.03, 0.03) };
		body[16] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.03, 0.1), put(-0.065, 0.03, 0.1),
				put(-0.065, 0.01, 0.11), put(-0.04, 0.01, 0.11) };
		body[17] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.03), put(-0.04, 0.03, 0.03),
				put(-0.04, 0.01, 0.029), put(-0.065, 0.01, 0.029) };
		body[18] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.065, 0.03, 0.03),
				put(-0.065, 0.01, 0.029), put(-0.065, 0.01, 0.11) };
		body[19] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.01, 0.11), put(-0.04, 0.01, 0.029),
				put(-0.04, 0.03, 0.03), put(-0.04, 0.03, 0.1) };
		body[20] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, 0.01, 0.029),
				put(-0.065, -0.01, 0.031), put(-0.065, -0.01, 0.1) };
		body[21] = new Polygon3D(v, put(-0.065, 0.03, 0.11), put(-0.065, 0.03,
				0.029), put(-0.065, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.04, -0.01, 0.1), put(-0.04, -0.01, 0.031),
				put(-0.04, 0.01, 0.029), put(-0.04, 0.01, 0.11) };
		body[22] = new Polygon3D(v, put(-0.04, 0.03, 0.11), put(-0.04, 0.03,
				0.029), put(-0.04, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, -0.01, 0.1),
				put(-0.04, -0.01, 0.1), put(-0.04, 0.01, 0.11) };
		body[23] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, -0.01, 0.031), put(-0.065, 0.01, 0.029),
				put(-0.04, 0.01, 0.029), put(-0.04, -0.01, 0.031) };
		body[24] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		start.set(tempVector1);
		start = put(0.105, 0, -0.12);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.04, 0.03, 0.1),
				put(-0.04, 0.03, 0.03), put(-0.065, 0.03, 0.03) };
		body[25] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.03, 0.1), put(-0.065, 0.03, 0.1),
				put(-0.065, 0.01, 0.11), put(-0.04, 0.01, 0.11) };
		body[26] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.03), put(-0.04, 0.03, 0.03),
				put(-0.04, 0.01, 0.029), put(-0.065, 0.01, 0.029) };
		body[27] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.065, 0.03, 0.03),
				put(-0.065, 0.01, 0.029), put(-0.065, 0.01, 0.11) };
		body[28] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.01, 0.11), put(-0.04, 0.01, 0.029),
				put(-0.04, 0.03, 0.03), put(-0.04, 0.03, 0.1) };
		body[29] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, 0.01, 0.029),
				put(-0.065, -0.01, 0.031), put(-0.065, -0.01, 0.1) };
		body[30] = new Polygon3D(v, put(-0.065, 0.03, 0.11), put(-0.065, 0.03,
				0.029), put(-0.065, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.04, -0.01, 0.1), put(-0.04, -0.01, 0.031),
				put(-0.04, 0.01, 0.029), put(-0.04, 0.01, 0.11) };
		body[31] = new Polygon3D(v, put(-0.04, 0.03, 0.11), put(-0.04, 0.03,
				0.029), put(-0.04, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, -0.01, 0.1),
				put(-0.04, -0.01, 0.1), put(-0.04, 0.01, 0.11) };
		body[32] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, -0.01, 0.031), put(-0.065, 0.01, 0.029),
				put(-0.04, 0.01, 0.029), put(-0.04, -0.01, 0.031) };
		body[33] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		start.set(tempVector1);
		start = put(0.105, 0, 0.01);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.04, 0.03, 0.1),
				put(-0.04, 0.03, 0.03), put(-0.065, 0.03, 0.03) };
		body[34] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.03, 0.1), put(-0.065, 0.03, 0.1),
				put(-0.065, 0.01, 0.11), put(-0.04, 0.01, 0.11) };
		body[35] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.03), put(-0.04, 0.03, 0.03),
				put(-0.04, 0.01, 0.029), put(-0.065, 0.01, 0.029) };
		body[36] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.03, 0.1), put(-0.065, 0.03, 0.03),
				put(-0.065, 0.01, 0.029), put(-0.065, 0.01, 0.11) };
		body[37] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.04, 0.01, 0.11), put(-0.04, 0.01, 0.029),
				put(-0.04, 0.03, 0.03), put(-0.04, 0.03, 0.1) };
		body[38] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, 0.01, 0.029),
				put(-0.065, -0.01, 0.031), put(-0.065, -0.01, 0.1) };
		body[39] = new Polygon3D(v, put(-0.065, 0.03, 0.11), put(-0.065, 0.03,
				0.029), put(-0.065, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.04, -0.01, 0.1), put(-0.04, -0.01, 0.031),
				put(-0.04, 0.01, 0.029), put(-0.04, 0.01, 0.11) };
		body[40] = new Polygon3D(v, put(-0.04, 0.03, 0.11), put(-0.04, 0.03,
				0.029), put(-0.04, -0.01, 0.11), Main.textures[12], 1, 1, 6);

		v = new Vector[] { put(-0.065, 0.01, 0.11), put(-0.065, -0.01, 0.1),
				put(-0.04, -0.01, 0.1), put(-0.04, 0.01, 0.11) };
		body[41] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		v = new Vector[] { put(-0.065, -0.01, 0.031), put(-0.065, 0.01, 0.029),
				put(-0.04, 0.01, 0.029), put(-0.04, -0.01, 0.031) };
		body[42] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 0.3,
				0.5, 6);

		for (int i = 0; i < body.length; i++)
			bodyInvisible[i] = new Polygon3D(body[i].getVertex3D(), new Vector(0, 0,
					0), new Vector(0, 0, 0), new Vector(0, 0, 0), null, 1, 1, 8);

		start.set(tempVector1);
		start.add(-0.015, 0, -0.01);
		start.y = -1;
		v = new Vector[] { put(-0.12, 0, 0.14), put(0.12, 0, 0.14),
				put(0.12, 0, -0.14), put(-0.12, 0, -0.14) };
		shadowBody = new Polygon3D(v, v[0], v[1], v[3], Main.textures[36], 1,
				1, 2);

		start.set(tempVector1);
		turretCenter = put(0, 0.08, -0.0);
	}

	private void makeTurret() {
		turret = new Polygon3D[66];
		turretInvisible = new Polygon3D[66];
		Vector v[];
		start = turretCenter.myClone();

		xDirection = new Vector(1, 0, 0);
		yDirection = new Vector(0, 1, 0);
		zDirection = new Vector(0, 0, 1.1);

		xDirection.rotate_XZ(turretAngle);
		zDirection.rotate_XZ(turretAngle);

		double r1 = 0.028;
		double r2 = 0.017;
		double r3 = 0.022;
		double theta = Math.PI / 16;

		int r = 56 / 8;
		int g = 79 / 8;
		int b = 167 / 8;
		short color = (short) ((int) r << 10 | (int) g << 5 | (int) b);

		for (int i = 0; i < 32; i++) {
			v = new Vector[] {
					put(r2 * Math.cos(i * theta), r2 * Math.sin(i * theta),
							-0.07),
					put(r2 * Math.cos((i + 1) * theta),
							r2 * Math.sin((i + 1) * theta), -0.07),
					put(r1 * Math.cos((i + 1) * theta),
							r1 * Math.sin((i + 1) * theta), 0.03),
					put(r1 * Math.cos(i * theta), r1 * Math.sin(i * theta),
							0.03) };
			turret[i] = new Polygon3D(v, v[0], v[1], v[3], null, 0.001, 0.01, 7);
			turretInvisible[i] = new Polygon3D(v, v[0], v[1], v[3], null, 1, 1,
					8);

			turret[i].color = color;
		}

		for (int i = 0; i < 32; i++) {
			v = new Vector[] {
					put(r1 * Math.cos(i * theta), r1 * Math.sin(i * theta),
							0.03),
					put(r1 * Math.cos((i + 1) * theta),
							r1 * Math.sin((i + 1) * theta), 0.03),
					put(r3 * Math.cos((i + 1) * theta),
							r3 * Math.sin((i + 1) * theta), 0.07),
					put(r3 * Math.cos(i * theta), r3 * Math.sin(i * theta),
							0.07) };
			turret[i + 32] = new Polygon3D(v, v[0], v[1], v[3], null, 0.001,
					0.01, 7);
			turretInvisible[i + 32] = new Polygon3D(v, v[0], v[1], v[3], null,
					1, 1, 8);
			turret[i + 32].color = color;
		}

		v = new Vector[32];
		for (int i = 1; i < 33; i++)
			v[32 - i] = put(r2 * Math.cos(i * theta), r2 * Math.sin(i * theta),
					-0.07);
		turret[64] = new Polygon3D(v, v[0], v[1], v[3], null, 1, 1, 7);
		turretInvisible[64] = new Polygon3D(v, v[0], v[1], v[3], null, 1, 1, 8);
		turret[64].color = color;

		v = new Vector[32];
		for (int i = 1; i < 33; i++)
			v[i - 1] = put(r3 * Math.cos(i * theta), r3 * Math.sin(i * theta),
					0.07);
		turret[65] = new Polygon3D(v, v[0], v[1], v[3], null, 1, 1, 7);
		turretInvisible[65] = new Polygon3D(v, v[0], v[1], v[3], null, 1, 1, 8);
		turret[65].color = color;

		start.add(-0.03, 0, -0.025);
		start.y = -1;
		v = new Vector[] { put(-0.14, 0, 0.14), put(0.14, 0, 0.14),
				put(0.14, 0, -0.14), put(-0.14, 0, -0.14) };
		shadowTurret = new Polygon3D(v, v[0], v[1], v[3], Main.textures[37], 1,
				1, 2);

	}

	public void update() {
		if ((Main.timer + randomNumber1 * 3) % 3000 == 0) {
			if (randomNumber2 > 50)
				randomNumber2 = 50;
			else
				randomNumber2 = 51;
		}

		if (countDownToDeath <= 0 && active && !Main.gamePaused)
			processAI();

		if (aimLeft) {
			if (Math.abs(turretAngle - targetAngle) <= 3) {
				turretAngleDelta = targetAngle - turretAngle;
				turretAngle += turretAngleDelta;
				if (turretAngleDelta < 0)
					turretAngleDelta += 360;
			} else {
				turretAngleDelta = 3;
				turretAngle += 3;
			}
			if (turretAngle >= 360)
				turretAngle -= 360;
		} else if (aimRight) {
			if (Math.abs(turretAngle - targetAngle) <= 3) {
				turretAngleDelta = targetAngle - turretAngle;
				turretAngle += turretAngleDelta;
				if (turretAngleDelta < 0)
					turretAngleDelta += 360;
			} else {
				turretAngleDelta = 357;
				turretAngle -= 3;
			}
			if (turretAngle < 0)
				turretAngle += 360;
		}

		if (forward) {
			int delta = targetAngleBody - bodyAngle;
			if (Math.abs(delta) < 5 || Math.abs(delta) > 355) {
				bodyAngle = targetAngleBody;
				bodyAngleDelta = (delta + 720) % 360;
				displacement.set(0, 0, 0.01);
				displacement.rotate_XZ(bodyAngle);
			} else {
				displacement.set(0, 0, 0);
				if (delta > 0) {
					if (delta < 180)
						bodyAngleDelta = 5;
					else
						bodyAngleDelta = 355;
				}
				if (delta < 0) {
					if (delta > -180)
						bodyAngleDelta = 355;
					else
						bodyAngleDelta = 5;
				}

				bodyAngle = (bodyAngle + bodyAngleDelta) % 360;
			}
		}

		int newPosition = (int) (boundary2D.xPos * 4)
				+ (129 - (int) (boundary2D.yPos * 4)) * 80;
		if (!ObstacleMap.isOccupied(newPosition)) {
			ObstacleMap.removeObstacle2(position);
			ObstacleMap.registerObstacle2(this, newPosition);
			position = newPosition;
		}

		centre.add(displacement);

		boundary2D.update(displacement);

		tempCentre.set(centre);
		tempCentre.y = -1;
		tempCentre.subtract(Camera.getPosition());
		tempCentre.rotate_XZ(Camera.getXZ_angle());
		tempCentre.rotate_YZ(Camera.getYZ_angle());
		tempCentre.updateLocation();

		visible = true;
		if (tempCentre.z < 0.9 || tempCentre.screenY < -10
				|| tempCentre.screenX < -400 || tempCentre.screenX > 800) {
			visible = false;
			isVisiblePreviousFrame = false;
		}

		if (visible) {
			if (isVisiblePreviousFrame == false) {
				makeBody();
				makeTurret();
				isVisiblePreviousFrame = true;
			}
		}

		if (visible) {
			ModelDrawList.register(this);
			if (countDownToDeath < 3) {

				for (int i = 0; i < body.length; i++) {
					body[i].origin.add(displacement);
					body[i].origin.subtract(centre);
					body[i].origin.rotate_XZ(bodyAngleDelta);
					body[i].origin.add(centre);

					body[i].bottomEnd.add(displacement);
					body[i].bottomEnd.subtract(centre);
					body[i].bottomEnd.rotate_XZ(bodyAngleDelta);
					body[i].bottomEnd.add(centre);

					body[i].rightEnd.add(displacement);
					body[i].rightEnd.subtract(centre);
					body[i].rightEnd.rotate_XZ(bodyAngleDelta);
					body[i].rightEnd.add(centre);

					for (int j = 0; j < body[i].getVertex3D().length; j++) {
						body[i].getVertex3D()[j].add(displacement);
						body[i].getVertex3D()[j].subtract(centre);
						body[i].getVertex3D()[j].rotate_XZ(bodyAngleDelta);
						body[i].getVertex3D()[j].add(centre);
					}

					body[i].findRealNormal();
					body[i].findDiffuse();

					body[i].update();
					bodyInvisible[i].update();
				}
				Geometry.sortPolygons(body, 2);

				tempVector1.set(centre);
				tempVector1.add(-0.015, 0, -0.01);
				shadowBody.origin.add(displacement);
				shadowBody.origin.subtract(tempVector1);
				shadowBody.origin.rotate_XZ(bodyAngleDelta);
				shadowBody.origin.add(tempVector1);

				shadowBody.bottomEnd.add(displacement);
				shadowBody.bottomEnd.subtract(tempVector1);
				shadowBody.bottomEnd.rotate_XZ(bodyAngleDelta);
				shadowBody.bottomEnd.add(tempVector1);

				shadowBody.rightEnd.add(displacement);
				shadowBody.rightEnd.subtract(tempVector1);
				shadowBody.rightEnd.rotate_XZ(bodyAngleDelta);
				shadowBody.rightEnd.add(tempVector1);

				for (int j = 0; j < shadowBody.getVertex3D().length; j++) {
					shadowBody.getVertex3D()[j].add(displacement);
					shadowBody.getVertex3D()[j].subtract(tempVector1);
					shadowBody.getVertex3D()[j].rotate_XZ(bodyAngleDelta);
					shadowBody.getVertex3D()[j].add(tempVector1);
				}

				shadowBody.update();
				Rasterizer.rasterize(shadowBody);

				turretCenter.add(displacement);

				for (int i = 0; i < turret.length; i++) {
					turret[i].origin.add(displacement);
					turret[i].origin.subtract(turretCenter);
					turret[i].origin.rotate_XZ(turretAngleDelta);
					turret[i].origin.add(turretCenter);

					turret[i].bottomEnd.add(displacement);
					turret[i].bottomEnd.subtract(turretCenter);
					turret[i].bottomEnd.rotate_XZ(turretAngleDelta);
					turret[i].bottomEnd.add(turretCenter);

					turret[i].rightEnd.add(displacement);
					turret[i].rightEnd.subtract(turretCenter);
					turret[i].rightEnd.rotate_XZ(turretAngleDelta);
					turret[i].rightEnd.add(turretCenter);

					for (int j = 0; j < turret[i].getVertex3D().length; j++) {
						turret[i].getVertex3D()[j].add(displacement);
						turret[i].getVertex3D()[j].subtract(turretCenter);
						turret[i].getVertex3D()[j].rotate_XZ(turretAngleDelta);
						turret[i].getVertex3D()[j].add(turretCenter);

					}

					turret[i].findRealNormal();
					turret[i].findDiffuse();

					turret[i].update();
					turretInvisible[i].update();
				}

				tempVector1.set(turretCenter);
				tempVector1.add(-0.03, 0, -0.025);

				shadowTurret.origin.add(displacement);
				shadowTurret.origin.subtract(tempVector1);
				shadowTurret.origin.rotate_XZ(turretAngleDelta);
				shadowTurret.origin.add(tempVector1);

				shadowTurret.bottomEnd.add(displacement);
				shadowTurret.bottomEnd.subtract(tempVector1);
				shadowTurret.bottomEnd.rotate_XZ(turretAngleDelta);
				shadowTurret.bottomEnd.add(tempVector1);

				shadowTurret.rightEnd.add(displacement);
				shadowTurret.rightEnd.subtract(tempVector1);
				shadowTurret.rightEnd.rotate_XZ(turretAngleDelta);
				shadowTurret.rightEnd.add(tempVector1);

				for (int j = 0; j < shadowTurret.getVertex3D().length; j++) {
					shadowTurret.getVertex3D()[j].add(displacement);
					shadowTurret.getVertex3D()[j].subtract(tempVector1);
					shadowTurret.getVertex3D()[j].rotate_XZ(turretAngleDelta);
					shadowTurret.getVertex3D()[j].add(tempVector1);
				}
				shadowTurret.update();
				Rasterizer.rasterize(shadowTurret);
			}

		}

		if (currentCoolDown > 0 && !Main.gamePaused)
			currentCoolDown--;

		if (firing) {

			if (currentCoolDown == 0) {
				currentCoolDown = coolDown;
				Vector direction = new Vector(0, 0, 1);
				direction.rotate_XZ(turretAngle);
				direction.scale(0.1);
				new RailgunTail(new Vector(centre.x + direction.x,
						centre.y + 0.08, centre.z + direction.z), turretAngle,
						true);

			}
		}

		if (t <= 30 && visible) {
			int alpha = 255 * t / 30;
			for (int i = 0; i < body.length; i++)
				body[i].alpha = alpha;

			for (int i = 0; i < turret.length; i++)
				turret[i].alpha = alpha;
			shadowBody.variation = (short) (alpha / 12);
			shadowTurret.variation = (short) (alpha / 12);
		}

		if (HP < 8) {
			if (Smoke == null) {
				Smoke = new Smoke(this);
			} else {
				if (visible)
					Smoke.update();
			}
		}

		if (HP <= 0) {
			countDownToDeath++;
			if (countDownToDeath >= 3) {
				if (countDownToDeath == 3) {
					Projectiles.register(new Explosion(centre.x, centre.y,
							centre.z, 1.7));
					PowerUps.register(new PowerUp(centre.x, -0.875, centre.z, 3));
				}
				ObstacleMap.removeObstacle2(position);
				Smoke.stopped = true;
			}
			if (countDownToDeath >= 40)
				lifeSpan = 0;
		}

		forward = false;
		aimRight = false;
		aimLeft = false;
		bodyAngleDelta = 0;
		turretAngleDelta = 0;
		displacement.reset();
		firing = false;
		if (Main.timer % 10 == 0)
			unstuck = false;
	}

	private void processAI() {
		tempVector1.set(centre);
		tempVector1.subtract(PlayerTank.bodyCenter);
		distance = tempVector1.getLength();

		if (distance < 2)
			engaged = true;

		if (distance > 4.5) {
			engaged = false;
			targetAngle = bodyAngle;
			int AngleDelta = turretAngle - targetAngle;
			if (AngleDelta > 0) {
				if (AngleDelta < 180)
					aimRight = true;
				else
					aimLeft = true;
			} else if (AngleDelta < 0) {
				if (AngleDelta > -180)
					aimLeft = true;
				else
					aimRight = true;
			}
		} else {

			if (engaged) {
				if ((Main.timer) % 5 == 0)
					ObstacleMap.alertNearbyTanks(position);

				tempVector1.set(centre);
				tempVector2.set(PlayerTank.bodyCenter);
				tempVector2.subtract(tempVector1);
				tempVector2.unit();
				tempVector2.scale(0.125);

				clearToShoot = true;
				double d = 0;
				int obstacleType = -1;
				for (int i = 0; i < 30 && d < distance; i++, tempVector1
						.add(tempVector2), d += 0.125) {
					Model temp = ObstacleMap.isOccupied2(tempVector1);
					if (temp == null)
						continue;
					obstacleType = temp.getType();
					if (temp.getType() == 1) {
						break;
					} else {
						clearToShoot = false;
						break;
					}

				}

				if (clearToShoot) {
					targetAngle = 90 + (int) (180 * Math
							.atan((centre.z - PlayerTank.bodyCenter.z)
									/ (centre.x - PlayerTank.bodyCenter.x)) / Math.PI);
					if (PlayerTank.bodyCenter.x > turretCenter.x
							&& targetAngle <= 180)
						targetAngle += 180;

				} else {
					targetAngle = bodyAngle;

				}

				int AngleDelta = turretAngle - targetAngle;
				if (Math.abs(AngleDelta) < 3 && clearToShoot && distance < 1.7) {
					firing = true;

				}

				if (clearToShoot && distance < 1.7) {
					if (t > 0)
						t--;
				} else {
					if (t < 30)
						t++;
				}

				if (AngleDelta > 0) {
					if (AngleDelta < 180)
						aimRight = true;
					else
						aimLeft = true;

				} else if (AngleDelta < 0) {
					if (AngleDelta > -180)
						aimLeft = true;
					else
						aimRight = true;
				}

				forward = true;
				if (clearToShoot && distance < 1.6) {
					forward = false;
				}

				if (unstuck && distance > 0.8) {
					forward = true;
					ObstacleMap.giveWay(this, position);

				}

				if (forward) {
					targetAngleBody = 90 + (int) (180 * Math
							.atan((centre.z - PlayerTank.bodyCenter.z)
									/ (centre.x - PlayerTank.bodyCenter.x)) / Math.PI);
					if (PlayerTank.bodyCenter.x > centre.x
							&& targetAngleBody <= 180)
						targetAngleBody += 180;

					if (!clearToShoot
							&& (distance < 1.2 || (obstacleType == 6 && distance < 2.5))
							|| stuckCount == 10) {

						if (stuckCount == 10) {
							if (randomNumber2 > 50)
								randomNumber2 = 50;
							else
								randomNumber2 = 51;
							stuckCount = 0;
						}

						if (randomNumber2 > 50)
							targetAngleBody += 90;
						else
							targetAngleBody -= 90;

						targetAngleBody = (targetAngleBody + 360) % 360;
					}

					displacement.set(0, 0, 0.01);
					displacement.rotate_XZ(targetAngleBody);
					boundary2D.update(displacement);
					int newPosition = (int) (boundary2D.xPos * 4)
							+ (129 - (int) (boundary2D.yPos * 4)) * 80;
					boolean canMove = true;
					if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
						forward = false;
						canMove = false;
					} else if (ObstacleMap.collideWithObstacle2(this,
							newPosition)) {
						forward = false;
						canMove = false;
					}
					displacement.scale(-1);
					boundary2D.update(displacement);
					displacement.reset();

					if (!canMove) {
						if (unstuck) {
							ObstacleMap.giveWay(this, position);
						}

						targetAngleBody = targetAngle;
						int angle1 = targetAngleBody - targetAngleBody % 90;
						int angle2 = angle1 + 90;

						angle2 = angle2 % 360;

						boolean canMoveAngle1 = true;
						boolean canMoveAngle2 = true;

						displacement.set(0, 0, 0.01);
						displacement.rotate_XZ(angle1);
						boundary2D.update(displacement);
						newPosition = (int) (boundary2D.xPos * 4)
								+ (129 - (int) (boundary2D.yPos * 4)) * 80;
						if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
							canMoveAngle1 = false;
						} else if (ObstacleMap.collideWithObstacle2(this,
								newPosition)) {
							canMoveAngle1 = false;
						}
						displacement.scale(-1);
						boundary2D.update(displacement);
						displacement.reset();

						displacement.set(0, 0, 0.01);
						displacement.rotate_XZ(angle2);
						boundary2D.update(displacement);
						newPosition = (int) (boundary2D.xPos * 4)
								+ (129 - (int) (boundary2D.yPos * 4)) * 80;
						if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
							canMoveAngle2 = false;
						} else if (ObstacleMap.collideWithObstacle2(this,
								newPosition)) {
							canMoveAngle2 = false;
						}
						displacement.scale(-1);
						boundary2D.update(displacement);
						displacement.reset();

						if (canMoveAngle1 && !canMoveAngle2) {
							targetAngleBody = angle1;
							forward = true;

							ObstacleMap.giveWay(this, position);
						} else if (!canMoveAngle1 && canMoveAngle2) {
							targetAngleBody = angle2;
							forward = true;

							ObstacleMap.giveWay(this, position);
						} else if (canMoveAngle1 && canMoveAngle2) {
							if (Math.abs(angle1 - targetAngleBody) < Math
									.abs(angle2 - targetAngleBody)) {
								targetAngleBody = angle1;

							} else {
								targetAngleBody = angle2;

							}
							forward = true;

						} else {

							stuckCount = 10;

							ObstacleMap.giveWay(this, position);

						}

						if ((previousTargetAngleBody + 180) % 360 == targetAngleBody) {
							targetAngleBody = previousTargetAngleBody;
						}

					}
					displacement.set(0, 0, 0.01);
					displacement.rotate_XZ(targetAngleBody);
					boundary2D.update(displacement);
					newPosition = (int) (boundary2D.xPos * 4)
							+ (129 - (int) (boundary2D.yPos * 4)) * 80;

					if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
						forward = false;

					} else if (ObstacleMap.collideWithObstacle2(this,
							newPosition)) {
						forward = false;

					}
					displacement.scale(-1);
					boundary2D.update(displacement);
					displacement.reset();
				}
			}
			previousTargetAngleBody = targetAngleBody;
		}
	}

	public void draw() {
		if (countDownToDeath < 3) {

			if (t != 30) {
				for (int i = 0; i < body.length; i++) {
					body[i].draw();
				}
			}

			if (t != 30) {
				for (int i = 0; i < turret.length; i++) {
					turret[i].draw();
				}
			}

			if (t != 0) {

				int x = Rasterizer.xOffset;
				int scale = Rasterizer.scale;
				Rasterizer.scale = 30 / t;
				if (t != 30) {

					Rasterizer.xOffset = 128 * t / 30;
				}

				for (int i = 0; i < turret.length; i++) {
					turretInvisible[i].draw();
				}

				for (int i = 0; i < body.length; i++) {
					bodyInvisible[i].draw();
				}
				Rasterizer.xOffset = x;
				Rasterizer.scale = scale;
			}

		}

		if (Smoke != null && visible)
			Smoke.draw();
	}

	public Rectangle2D getBoundary2D() {
		return boundary2D;
	}

	public void damage(int damagePoint) {
		if (damagePoint == -1) {
			active = true;
		} else {
			HP -= damagePoint;
			engaged = true;
		}
	}
}