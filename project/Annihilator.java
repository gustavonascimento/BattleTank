package project;
/**
 *	This class is part of BattleTank 2.
 *
 *  BattleTank 2 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BattleTank 2 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BattleTank 2.  If not, see <http://www.gnu.org/licenses/>
 */

import java.util.logging.Logger;

/**
 * Class to create objects Anniquilator type, which are contained values ​​and
 * methods for this purpose
 */
public class Annihilator extends SolidObject {

	/**
	 * This is a constructor method. X, y and z are coordinates for construction
	 * of the annihilator in the space.
	 * 
	 * @param x
	 * @param y
	 * @param y
	 * @param angle
	 */

	public Annihilator(double x, double y, double z, int angle) {

		assert (x == 12.625 && y == -0.975 && (z == 19.375 || z == 21.375) && angle == 90);
		assert (bodyCenter == null);

		final double LENGHTRECTANGLE = 0.23;

		final double HEIGHTRECTANGLE = 0.23;

		final double XPOS = x - 0.115;

		final double YPOS = z + 0.115;

		final double LENGHT = 0.1;

		final double HEIGHT = 0.25;

		final double WIDTH = 0.1;

		start = new Vector(x, y, z);

		xDirection = new Vector(1, 0, 0);

		yDirection = new Vector(0, 1, 0);

		zDirection = new Vector(0, 0, 1);

		modelType = 2;
		makeBoundary(LENGHT, HEIGHT, WIDTH);

		boundary2D = new Rectangle2D(XPOS, YPOS, LENGHTRECTANGLE,
				HEIGHTRECTANGLE);

		position = (int) (x * 4) + (129 - (int) (z * 4)) * 80;
		desiredPosition = position;
		ObstacleMap.registerObstacle2(this, position);

		// Find centre of the model in world coordinate.
		findCenter();

		bodyCenter = centre;

		bodyAngle = angle;

		turretAngle = angle;

		makeBody();
		makeTurret();

		randomNumber1 = GameData.getRandom();

		// Annihilator tank has 400 hit points
		HP = 400;

		lifeSpan = 1;
	}

	/**
	 * This method is responsible for updating the annihilator in the scenario,
	 * according to the actions in the game.
	 */
	public void update() {
		assert (displacement != null);
		assert (turret != null);
		assert (centre != null);
		assert (boundary2D != null);

		if ((Main.timer + randomNumber1 * 3) % 1000 == 0) {
			if (randomNumber2 > 50) {
				randomNumber2 = 50;
			} else {
				randomNumber2 = 51;
			}
		} else {
			// Does nothing.
		}

		if (countDownToDeath <= 0 && active && !Main.gamePaused) {
			processAI();
		} else {
			// Does nothing.
		}

		if (aimLeft) {
			if (Math.abs(turretAngle - targetAngle) <= 3) {

				turretAngleDelta = targetAngle - turretAngle;
				turretAngle += turretAngleDelta;
				if (turretAngleDelta < 0) {
					turretAngleDelta += 360;
				} else {
					// Does nothing.
				}
			} else {
				turretAngleDelta = 3;
				turretAngle += 3;
			}
			if (turretAngle >= 360) {
				turretAngle -= 360;
			} else {
				// Does nothing.
			}
		} else if (aimRight) {
			if (Math.abs(turretAngle - targetAngle) <= 3) {
				turretAngleDelta = targetAngle - turretAngle;
				turretAngle += turretAngleDelta;
				if (turretAngleDelta < 0) {
					turretAngleDelta += 360;
				} else {
					// Does nothing.
				}
			} else {
				turretAngleDelta = 357;
				turretAngle -= 3;
			}
			if (turretAngle < 0) {
				turretAngle += 360;
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}

		if (forward) {
			// Move forward
			int delta = targetAngleBody - bodyAngle;
			if (Math.abs(delta) < 5 || Math.abs(delta) > 355) {
				bodyAngle = targetAngleBody;

				bodyAngleDelta = (delta + 720) % 360;
				displacement.set(0, 0, 0.01);
				displacement.rotate_XZ(bodyAngle);
			} else {
				displacement.set(0, 0, 0);
				if (delta > 0) {
					if (delta < 180) {
						bodyAngleDelta = 5;
					} else {
						bodyAngleDelta = 355;
					}
				} else if (delta < 0) {
					if (delta > -180) {
						bodyAngleDelta = 355;
					} else {
						bodyAngleDelta = 5;
					}
				} else {
					// Does nothing.
				}

				bodyAngle = (bodyAngle + bodyAngleDelta) % 360;
			}
		} else {
			// Does nothing.
		}
		// Update centre
		centre.add(displacement);
		// Update bundary2D
		boundary2D.update(displacement);

		// Update location in the 2d tile map

		// Validating movement is already done in process AI part
		int newPosition = (int) (boundary2D.xPos * 4)
				+ (129 - (int) (boundary2D.yPos * 4)) * 80;
		if (!ObstacleMap.isOccupied(newPosition)) {
			ObstacleMap.removeObstacle2(position);
			ObstacleMap.registerObstacle2(this, newPosition);
			position = newPosition;
			desiredPosition = newPosition;
		} else if (!ObstacleMap.isOccupied(desiredPosition)) {
			ObstacleMap.removeObstacle2(position);
			ObstacleMap.registerObstacle2(this, desiredPosition);
			position = desiredPosition;
		} else {
			desiredPosition = newPosition;
		}

		// Find centre in camera coordinate

		tempCentre.set(centre);
		tempCentre.y = -1;
		tempCentre.subtract(Camera.getPosition());
		tempCentre.rotate_XZ(Camera.getXZ_angle());
		tempCentre.rotate_YZ(Camera.getYZ_angle());
		tempCentre.updateLocation();

		// Test whether the model is visible by comparing the 2D position of its
		// centre point with the screen
		visible = true;

		if (tempCentre.z < 0.9 || tempCentre.screenY < -10
				|| tempCentre.screenX < -400 || tempCentre.screenX > 800) {
			visible = false;
			isVisiblePreviousFrame = false;
		} else {
			// Does nothing.
		}

		// If tank is not visible in the previous frame, its need to be
		// reconstructed
		if (visible) {
			if (isVisiblePreviousFrame == false) {
				// Recreate body and turret polygons
				makeBody();
				makeTurret();
				isVisiblePreviousFrame = true;
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}

		// If visible then update the geometry to camera coordinate

		if (visible) {
			ModelDrawList.register(this);

			if (countDownToDeath < 3) {

				// Update body polygons

				for (int i = 0; i < body.length; i++) {
					// Perform vertex updates in world coordinate
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
					// Transform the polygon into camera coordinate
					body[i].update();
				}

				// Update shadow for tank body

				tempVector1.set(centre);
				tempVector1.add(-0.03, 0, -0.04);
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
				// Update turret center
				turretCenter.add(displacement);

				// Update turret polygons

				for (int i = 0; i < turret.length; i++) {
					// Perform vertex updates in world coordinate
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
					// Transform the polygon into camera coordinate
					turret[i].update();
				}
				// Update shadow for tank turret
				tempVector1.set(turretCenter);
				tempVector1.add(-0.03, 0, -0.04);

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
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}
		// Handle attack event
		if (coolDownShell > 0 && coolDownShell != 92 && !Main.gamePaused) {
			coolDownShell--;
		} else {
			// Does nothing.
		}

		if (coolDownRocket > 0 && coolDownRocket != 90 && !Main.gamePaused) {
			coolDownRocket--;
		} else {
			// Does nothing.
		}

		if (firingShell) {

			if (coolDownShell == 0) {
				coolDownShell = 100;
				// Calculate laser direction
				Vector tempVector1 = new Vector(0, 0, 1);
				tempVector1.rotate_XZ((turretAngle + 270) % 360);
				tempVector1.scale(0.035);
				Vector direction = new Vector(0, 0, 1);
				direction.rotate_XZ(turretAngle);
				direction.scale(0.1);
				direction.add(turretCenter);
				direction.add(tempVector1);
				Projectiles.register(new Shell(direction.x, direction.y,
						direction.z, turretAngle, true, 1));

			} else {
				// Does nothing.
			}

			if (coolDownShell == 92) {
				coolDownShell = 25;
				// Calculate shell direction
				Vector tempVector1 = new Vector(0, 0, 1);
				tempVector1.rotate_XZ((turretAngle + 270) % 360);
				tempVector1.scale(-0.035);
				Vector direction = new Vector(0, 0, 1);
				direction.rotate_XZ(turretAngle);
				direction.scale(0.1);
				direction.add(turretCenter);
				direction.add(tempVector1);
				Projectiles.register(new Shell(direction.x, direction.y,
						direction.z, turretAngle, true, 1));
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}

		if (firingRocket) {

			if (coolDownRocket == 0) {
				coolDownRocket = 100;
				// Calculate laser direction
				Vector tempVector1 = new Vector(0, 0, 1);
				tempVector1.rotate_XZ((turretAngle + 270) % 360);
				tempVector1.scale(0.095);
				Vector direction = new Vector(0, 0, 1);
				direction.rotate_XZ(turretAngle);
				direction.scale(0.05);
				direction.add(turretCenter);
				direction.add(tempVector1);

				Rocket r = new Rocket(direction.x, direction.y, direction.z,
						turretAngle, true);
				Projectiles.register(r);
			} else {
				// Does nothing.
			}

			if (coolDownRocket == 90) {
				coolDownRocket = 45;
				// Calculate shell direction
				Vector tempVector1 = new Vector(0, 0, 1);
				tempVector1.rotate_XZ((turretAngle + 270) % 360);
				tempVector1.scale(-0.095);
				Vector direction = new Vector(0, 0, 1);
				direction.rotate_XZ(turretAngle);
				direction.scale(0.05);
				direction.add(turretCenter);
				direction.add(tempVector1);
				Rocket rocket = new Rocket(direction.x, direction.y,
						direction.z, turretAngle, true);
				Projectiles.register(rocket);
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}

		if (HP <= 200) {
			if (Smoke == null) {
				Smoke = new Smoke(this);
			} else {
				if (visible) {
					Smoke.update();
				} else {
					// Does nothing
				}
			}
		} else {
			// Does nothing
		}

		if (HP <= 0) {
			countDownToDeath++;
			if (countDownToDeath >= 3) {
				if (countDownToDeath == 3) {
					Projectiles.register(new Explosion(centre.x, centre.y,
							centre.z, 2));

				} else {
					// Does nothing.
				}
				ObstacleMap.removeObstacle2(position);
				Smoke.stopped = true;

			} else {
				// Does nothing.
			}
			if (countDownToDeath >= 40) {
				lifeSpan = 0;
			} else {
				// Does nothing.
			}
		} else {
			// Does nothing.
		}

		// Reset action flag

		forward = false;
		aimRight = false;
		aimLeft = false;
		bodyAngleDelta = 0;
		turretAngleDelta = 0;
		displacement.reset();
		firingRocket = false;
		firingShell = false;
		if (Main.timer % 10 == 0) {
			unstuck = false;
		} else {
			// Does nothing.
		}
	}

	/** This method is responsible for drawing the body annihilator. */
	public void draw() {
		if (countDownToDeath < 3) {
			// Draw body
			for (int i = 0; i < body.length; i++) {
				body[i].draw();

			}
			// Draw turret
			for (int i = 0; i < turret.length; i++) {
				turret[i].draw();
			}
		} else {
			// Does nothing.
		}

		// Draw smoke tail

		if (Smoke != null && visible) {
			Smoke.draw();
		} else {
			// Does nothing.
		}
	}

	public boolean active = true;

	/**
	 * This method is responsible for specifying the loss of life of
	 * annihilator.
	 * 
	 * @param damagePoint
	 */

	public void damage(int damagePoint) {

		if (damagePoint > -1) {
			HP -= damagePoint;
			engaged = true;
		} else {
			active = true;
			engaged = true;
		}
		
	}

	/**
	 * This method is responsible for return the 2D boundary of the annihilator.
	 * 
	 * @return boundary2D
	 */
	public Rectangle2D getBoundary2D() {
		return boundary2D;
	}

	/** Starts a Logger to Annhilator class */
	private static final Logger LOG = Logger.getLogger(Annihilator.class
			.getName());

	/**
	 * Total angle that the body has rotated from the initial position. (in the
	 * x-z plane).
	 */
	private int bodyAngle = 0;

	/**
	 * Total angle that the turret has rotated from the initial position. (in
	 * the x-z plane).
	 */
	private int turretAngle = 0;

	/** Degrees the tank body has rotated in a frame. */
	private int bodyAngleDelta = 0;

	/** Degrees the tank turret has rotated in a frame. */
	private int turretAngleDelta = 0;

	/** The position index of the tank in the grid map. */
	private int position = 0;

	private int desiredPosition = 0;

	/** Time needed before a weapon can be fired again */
	private int coolDownShell = 33;

	/** Time needed before a weapon can be fired again. */
	private int coolDownRocket = 33;

	/** Angle between player tank and turret center. */
	private int targetAngle = 0;

	/** Angle between a target location and body center. */
	private int targetAngleBody = 0;

	/** TargetAngleBody of the previous frame. */
	private int previousTargetAngleBody = 0;

	/** A count down for death after hp = 0. */
	private int countDownToDeath = 0;

	/** Represent the time that medium tank has been in stuck status. */
	private int stuckCount = 0;

	private int randomNumber1 = 0;

	private int randomNumber2 = 0;

	/** Distance from player tank. */
	private double distance = 0;

	/** Go foward. */
	private boolean forward;

	/** The aim of the Tank moves to the right. */
	private boolean aimRight;

	/** The aim of the Tank moves to the left. */
	private boolean aimLeft;

	/** Shoots shell. */
	private boolean firingShell;

	/** Shoots Rocket. */
	private boolean firingRocket;

	/** It indicates whether the previous square is visible. */
	private boolean isVisiblePreviousFrame;

	private boolean engaged;

	/** It indicates whether this clear to shot. */
	private boolean clearToShoot;

	/** The center of the body in camera coordinate. */
	private Vector bodyCenter;

	/** Polygons for tank turret */
	private Polygon3D turret[];

	/** The shadow of tank body */
	private Polygon3D shadowBody;

	/** The shadow of tank turret */
	private Polygon3D shadowTurret;

	/** The center of the turret (pivot point for rotation). */
	private Vector turretCenter;

	/** Polygons for tank body. */
	private Polygon3D body[];

	/** Change in tank's position of each frame. */
	private Vector displacement = new Vector(0, 0, 0);

	/** Temporary vectors which will be used for vector arithmetic. */
	private Vector tempVector1 = new Vector(0, 0, 0);

	private Vector tempVector2 = new Vector(0, 0, 0);

	/** smoke tail will be visible if the tank's health is dropped to half. */
	private Smoke Smoke;

	/**
	 * This method is responsible to create the polygons of the body of
	 * annihilator.
	 */
	private void makeBody() {
		Vector v[];
		start = bodyCenter.myClone();

		xDirection = new Vector(0.95, 0, 0);
		yDirection = new Vector(0, 1, 0);
		zDirection = new Vector(0, 0, 1);

		xDirection.rotate_XZ(bodyAngle);
		zDirection.rotate_XZ(bodyAngle);

		body = new Polygon3D[19];

		v = new Vector[] { put(0.1, 0, 0.15), put(0.06, 0, 0.15),
				put(0.06, -0.04, 0.14), put(0.1, -0.04, 0.14) };
		body[0] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 1, 0.5,
				6);

		v = new Vector[] { put(-0.1, -0.04, 0.14), put(-0.06, -0.04, 0.14),
				put(-0.06, 0, 0.15), put(-0.1, 0, 0.15) };
		body[1] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 1, 0.5,
				6);

		v = new Vector[] { put(0.06, 0, -0.14), put(0.1, 0, -0.14),
				put(0.1, -0.04, -0.12), put(0.06, -0.04, -0.12) };
		body[2] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 1, 0.5,
				6);

		v = new Vector[] { put(-0.06, -0.04, -0.12), put(-0.1, -0.04, -0.12),
				put(-0.1, 0, -0.14), put(-0.06, 0, -0.14) };
		body[3] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[12], 1, 0.5,
				6);

		int i = 4;

		v = new Vector[] { put(0.06, 0.06, 0.13), put(0.06, 0.06, 0.08),
				put(0.06, -0.01, 0.08), put(0.06, -0.01, 0.15) };
		body[0 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 1.1, 6);

		v = new Vector[] { put(-0.06, -0.01, 0.15), put(-0.06, -0.01, 0.08),
				put(-0.06, 0.06, 0.08), put(-0.06, 0.06, 0.13) };
		body[1 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 1.1, 6);

		v = new Vector[] { put(-0.06, 0.06, 0.09), put(0.06, 0.06, 0.09),
				put(0.06, 0.06, -0.13), put(-0.06, 0.06, -0.13) };
		body[2 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 1.1, 6);

		v = new Vector[] { put(0.06, 0.06, 0.09), put(-0.06, 0.06, 0.09),
				put(-0.06, 0, 0.15), put(0.06, 0, 0.15) };
		body[3 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 0.4, 6);

		v = new Vector[] { put(-0.1, 0.06, -0.13), put(0.1, 0.06, -0.13),
				put(0.1, 0, -0.14), put(-0.1, 0, -0.14) };
		body[4 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 0.3, 6);

		v = new Vector[] { put(0.06, 0.06, 0.13), put(0.1, 0.06, 0.13),
				put(0.1, 0.06, -0.13), put(0.06, 0.06, -0.13) };
		body[5 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.3, 0.8, 6);

		v = new Vector[] { put(-0.06, 0.06, -0.13), put(-0.1, 0.06, -0.13),
				put(-0.1, 0.06, 0.13), put(-0.06, 0.06, 0.13) };
		body[6 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.3, 0.8, 6);

		v = new Vector[] { put(0.1, 0.06, 0.13), put(0.06, 0.06, 0.13),
				put(0.06, 0., 0.15), put(0.1, 0., 0.15) };
		body[7 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 1.1, 6);

		v = new Vector[] { put(-0.1, 0., 0.15), put(-0.06, 0., 0.15),
				put(-0.06, 0.06, 0.13), put(-0.1, 0.06, 0.13) };
		body[8 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 1.1, 6);

		v = new Vector[] { put(0.1, 0.06, -0.13), put(0.1, 0.06, 0.13),
				put(0.1, 0, 0.15), put(0.1, 0, -0.14) };
		body[9 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 0.2, 6);

		v = new Vector[] { put(-0.1, 0, -0.14), put(-0.1, 0, 0.15),
				put(-0.1, 0.06, 0.13), put(-0.1, 0.06, -0.13) };
		body[10 + i] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[58],
				0.8, 0.2, 6);

		v = new Vector[] { put(0.1, 0, 0.01), put(0.1, 0, 0.15),
				put(0.1, -0.04, 0.14), put(0.1, -0.04, 0.03) };
		body[11 + i] = new Polygon3D(v, put(0.1, 0.1, 0.03),
				put(0.1, 0.1, 0.13), put(0.1, -0.04, 0.03), Main.textures[12],
				1, 0.5, 6);

		v = new Vector[] { put(0.1, 0, -0.14), put(0.1, 0, -0.01),
				put(0.1, -0.04, -0.03), put(0.1, -0.04, -0.12) };
		body[12 + i] = new Polygon3D(v, put(0.1, 0.1, -0.15), put(0.1, 0.1,
				-0.01), put(0.1, -0.04, -0.15), Main.textures[12], 1, 0.5, 6);

		v = new Vector[] { put(-0.1, -0.04, 0.03), put(-0.1, -0.04, 0.14),
				put(-0.1, 0, 0.15), put(-0.1, 0, 0.01) };
		body[13 + i] = new Polygon3D(v, put(-0.1, 0.1, 0.03), put(-0.1, 0.1,
				0.13), put(-0.1, -0.04, 0.03), Main.textures[12], 1, 0.5, 6);

		v = new Vector[] { put(-0.1, -0.04, -0.12), put(-0.1, -0.04, -0.03),
				put(-0.1, 0, -0.01), put(-0.1, 0, -0.14) };
		body[14 + i] = new Polygon3D(v, put(-0.1, 0.1, -0.15), put(-0.1, 0.1,
				-0.01), put(-0.1, -0.04, -0.15), Main.textures[12], 1, 0.5, 6);

		turretCenter = put(0, 0.07, -0);

		// Create shadow for tank body
		start.add(-0.015, 0, -0.015);
		start.y = -1;
		v = new Vector[] { put(-0.3, 0, 0.3), put(0.3, 0, 0.3),
				put(0.3, 0, -0.3), put(-0.3, 0, -0.3) };
		shadowBody = new Polygon3D(v, v[0], v[1], v[3], Main.textures[14], 1,
				1, 2);

	}

	/** This method is responsible to create polygons for the tank turret. */
	private void makeTurret() {
		assert (turretCenter != null);
		// assert(turret != null);

		start = turretCenter.myClone();
		Vector v[];

		xDirection = new Vector(1.6, 0, 0);
		yDirection = new Vector(0, 1.4, 0);
		zDirection = new Vector(0, 0, 1.4);

		// Adjust orientation of the turret
		xDirection.rotate_XZ(turretAngle);
		zDirection.rotate_XZ(turretAngle);

		turret = new Polygon3D[23];

		v = new Vector[] { put(0.04, 0.035, 0.06), put(-0.04, 0.035, 0.06),
				put(-0.04, 0, 0.06), put(0.04, 0, 0.06) };
		turret[0] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.6,
				0.3, 6);

		v = new Vector[] { put(0.02, 0.025, 0.18), put(0.026, 0.015, 0.18),
				put(0.028, 0.015, 0.06), put(0.02, 0.025, 0.06) };
		turret[1] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[60], 0.1,
				1, 6);

		v = new Vector[] { put(0.02, 0.025, 0.06),
				put(-0.008 + 0.02, 0.015, 0.06),
				put(-0.006 + 0.02, 0.015, 0.18), put(0.02, 0.025, 0.18) };
		turret[2] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[60], 0.1,
				1, 6);

		v = new Vector[] { put(-0.02, 0.025, 0.18),
				put(0.006 - 0.02, 0.015, 0.18), put(0.008 - 0.02, 0.015, 0.06),
				put(-0.02, 0.025, 0.06) };
		turret[3] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[60], 0.1,
				1, 6);

		v = new Vector[] { put(-0.02, 0.025, 0.06), put(-0.028, 0.015, 0.06),
				put(-0.026, 0.015, 0.18), put(-0.02, 0.025, 0.18) };
		turret[4] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[60], 0.1,
				1, 6);

		v = new Vector[] { put(-0.04, 0.035, 0.06), put(0.04, 0.035, 0.06),
				put(0.05, 0.035, 0.04), put(0.05, 0.035, -0.03),
				put(0.03, 0.035, -0.07), put(-0.03, 0.035, -0.07),
				put(-0.05, 0.035, -0.03), put(-0.05, 0.035, 0.04) };
		turret[5] = new Polygon3D(v, put(-0.04, 0.035, 0.19), put(0.04, 0.035,
				0.19), put(-0.04, 0.035, 0.09), Main.textures[59], 0.6, 0.6, 6);

		v = new Vector[] { put(0.03, 0, -0.07), put(-0.03, 0, -0.07),
				put(-0.03, 0.035, -0.07), put(0.03, 0.035, -0.07) };
		turret[6] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.4,
				0.2, 6);

		v = new Vector[] { put(0.03, 0.035, -0.07), put(0.05, 0.035, -0.03),
				put(0.05, 0, -0.03), put(0.03, 0, -0.07) };
		turret[7] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.4,
				0.2, 6);

		v = new Vector[] { put(-0.03, 0, -0.07), put(-0.05, 0, -0.03),
				put(-0.05, 0.035, -0.03), put(-0.03, 0.035, -0.07) };
		turret[8] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.4,
				0.2, 6);

		v = new Vector[] { put(0.05, 0.035, -0.03), put(0.05, 0.035, 0.04),
				put(0.05, 0, 0.04), put(0.05, 0, -0.03) };
		turret[9] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.5,
				0.3, 6);

		v = new Vector[] { put(-0.05, 0, -0.03), put(-0.05, 0, 0.04),
				put(-0.05, 0.035, 0.04), put(-0.05, 0.035, -0.03) };
		turret[10] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.5,
				0.3, 6);

		v = new Vector[] { put(0.05, 0.035, 0.04), put(0.04, 0.035, 0.06),
				put(0.04, 0, 0.06), put(0.05, 0, 0.04) };
		turret[11] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.3,
				0.3, 6);

		v = new Vector[] { put(-0.05, 0, 0.04), put(-0.04, 0, 0.06),
				put(-0.04, 0.035, 0.06), put(-0.05, 0.035, 0.04) };
		turret[12] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[59], 0.3,
				0.3, 6);

		v = new Vector[] { put(-0.075, 0.05, 0.02), put(-0.05, 0.05, 0.02),
				put(-0.05, 0.05, -0.04), put(-0.075, 0.05, -0.04) };
		turret[13] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(-0.075, 0.05, 0.02), put(-0.075, 0.05, -0.04),
				put(-0.075, 0.02, -0.04), put(-0.075, 0.02, 0.02) };
		turret[14] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(-0.075, 0.05, -0.04), put(-0.05, 0.05, -0.04),
				put(-0.05, 0.02, -0.04), put(-0.075, 0.02, -0.04) };
		turret[15] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(-0.05, 0.05, -0.04), put(-0.05, 0.05, 0.02),
				put(-0.05, 0.035, 0.02), put(-0.05, 0.035, -0.04) };
		turret[16] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		int r = 150 / 8;
		int g = 150 / 8;
		int b = 150 / 8;
		short color = (short) ((int) r << 10 | (int) g << 5 | (int) b);

		v = new Vector[] { put(-0.075, 0.02, 0.02), put(-0.05, 0.02, 0.02),
				put(-0.05, 0.05, 0.02), put(-0.075, 0.05, 0.02) };
		turret[17] = new Polygon3D(v, v[0], v[1], v[3], null, 0.5, 0.5, 7);
		turret[17].color = color;

		v = new Vector[] { put(0.075, 0.05, -0.04), put(0.05, 0.05, -0.04),
				put(0.05, 0.05, 0.02), put(0.075, 0.05, 0.02) };
		turret[18] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(0.075, 0.02, 0.02), put(0.075, 0.02, -0.04),
				put(0.075, 0.05, -0.04), put(0.075, 0.05, 0.02) };
		turret[19] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(0.075, 0.02, -0.04), put(0.05, 0.02, -0.04),
				put(0.05, 0.05, -0.04), put(0.075, 0.05, -0.04) };
		turret[20] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(0.05, 0.035, -0.04), put(0.05, 0.035, 0.02),
				put(0.05, 0.05, 0.02), put(0.05, 0.05, -0.04) };
		turret[21] = new Polygon3D(v, v[0], v[1], v[3], Main.textures[35], 0.5,
				0.5, 6);

		v = new Vector[] { put(0.075, 0.05, 0.02), put(0.05, 0.05, 0.02),
				put(0.05, 0.02, 0.02), put(0.075, 0.02, 0.02) };
		turret[22] = new Polygon3D(v, v[0], v[1], v[3], null, 0.5, 0.5, 7);
		turret[22].color = color;

		// Create shadow for tank turret
		start.add(-0.03, 0, -0.04);
		start.y = -1;
		v = new Vector[] { put(-0.18, 0, 0.18), put(0.18, 0, 0.18),
				put(0.18, 0, -0.18), put(-0.18, 0, -0.18) };
		shadowTurret = new Polygon3D(v, v[0], v[1], v[3], Main.textures[61], 1,
				1, 2);

	}

	private void processAI() {
		assert (tempVector1 == null);
		assert (tempVector2 == null);
		assert (bodyCenter == null);

		// Calculate distance from player's tank
		try {
			tempVector1.set(centre);
			tempVector1.subtract(PlayerTank.bodyCenter);
			distance = tempVector1.getLength();
		} catch (NullPointerException erro) {
			LOG.severe("Null Point Error" + erro);
		}
		// Medium tank become aware of player's tank when the distance is less
		// than 2
		if (distance < 2) {
			engaged = true;
		} else {
			// Does nothing.
		}
		// Medium tank will stop chasing the player when the distance is greater
		// than 4
		if (distance > 6) {
			engaged = false;
			// Rotate the turret to the same angle as the body
			targetAngle = bodyAngle;
			int AngleDelta = turretAngle - targetAngle;
			if (AngleDelta > 0) {
				if (AngleDelta < 180) {
					aimRight = true;
				} else {
					aimLeft = true;
				}
			} else if (AngleDelta < 0) {
				if (AngleDelta > -180) {
					aimLeft = true;
				} else {
					aimRight = true;
				}
			} else {
				// Does nothing.
			}
		} else {
			if (engaged) {
				/*
				 * If medium tank is engaged with player, it will send alert to
				 * nearby tanks
				 */
				if ((Main.timer) % 5 == 0) {
					ObstacleMap.alertNearbyTanks(position);

					/*
					 * Test whether there is a type obstacle 2 between medium
					 * tank and player tank Firing a vision ray from medium tank
					 * toward player tank
					 */
				} else {
					// Does nothing.
				}

				try {
					tempVector1.set(bodyCenter);
					tempVector2.set(PlayerTank.bodyCenter);
					tempVector2.subtract(tempVector1);
					tempVector2.unit();
					tempVector2.scale(0.125);
				} catch (NullPointerException erro) {
					LOG.severe("Null Point Error" + erro);
				}

				// Find the angle between target and itself

				clearToShoot = true;
				int obstacleType = -1;
				double d = 0;
				try {
					for (int i = 0; (d < distance) && (i < 30); i++, tempVector1
							.add(tempVector2), d += 0.125) {
						Model temp = ObstacleMap.isOccupied2(tempVector1);
						if (temp == null) {
							continue;
						} else {
							// Does nothing.
						}
						obstacleType = temp.getType();
						if (obstacleType == 1) {
							break;
						} else {
							clearToShoot = false;
							break;
						}

					}
				} catch (NullPointerException erro) {
					LOG.severe("Null Point Error" + erro);
				}

				if (clearToShoot) {
					targetAngle = 90 + (int) (180 * Math
							.atan((centre.z - PlayerTank.bodyCenter.z)
									/ (centre.x - PlayerTank.bodyCenter.x)) / Math.PI);
					if (PlayerTank.bodyCenter.x > turretCenter.x
							&& targetAngle <= 180) {
						targetAngle += 180;
					} else {
						// Does nothing.
					}

				} else {
					targetAngle = bodyAngle;

				}
				// Calculate the difference between those 2 angles
				int AngleDelta = turretAngle - targetAngle;
				if (Math.abs(AngleDelta) < 3 && clearToShoot && distance < 1.7) {
					firingShell = true;
				} else {
					// Does nothing.
				}
				if (Math.abs(AngleDelta) < 3 && clearToShoot && distance < 3) {
					firingRocket = true;
				} else {
					// Does nothing.
				}
				// Aim at a target angle
				if (AngleDelta > 0) {
					if (AngleDelta < 180) {
						aimRight = true;
					} else {
						aimLeft = true;
					}
				} else if (AngleDelta < 0) {
					if (AngleDelta > -180) {
						aimLeft = true;
					} else {
						aimRight = true;
					}
				} else {
					forward = true;
				}
				/*
				 * Move to a target location medium tank will move towards
				 * player tank's position until distance is less than 1.4, or it
				 * detects a type 2 obstacle between itself and the player's
				 * tank
				 */
				
				if (clearToShoot && distance < 1.5) {
					if (distance < 1.4) {
						forward = false;
					} else {
						// Does nothing.
					}
					if (distance >= 1.4) {
						if (randomNumber2 > 50) {
							forward = false;
						} else {
							// Does nothing.
						}
					} else {
						// Does nothing.
					}
				} else {
					// Does nothing.
				}

				if (unstuck && distance > 0.8) {
					forward = true;
					ObstacleMap.giveWay(this, position);
				} else {
					// Does nothing.
				}

				if (forward) {
					targetAngleBody = 90 + (int) (180 * Math
							.atan((centre.z - PlayerTank.bodyCenter.z)
									/ (centre.x - PlayerTank.bodyCenter.x)) / Math.PI);
					if (PlayerTank.bodyCenter.x > centre.x
							&& targetAngleBody <= 180) {
						targetAngleBody += 180;
					} else {
						// Does nothing.
					}

					// The enemy tank will occasionly (~once every 10 secs)
					// perfom a
					// 90 degree change in moving angle if:
					// 1. it cant see the target tank and the target is within
					// 1.2
					// unit away
					// 2. it got stucked and the target is within 1.2 units away
					// 3. blocked by a wall and the target is within 3 units
					// away

					if (!clearToShoot
							&& (distance < 1.2 || (obstacleType == 6 && distance < 2.5))
							|| stuckCount == 10) {
						if (stuckCount == 10) {
							if (randomNumber2 > 50) {
								randomNumber2 = 50;
							} else {
								randomNumber2 = 51;
							}
							stuckCount = 0;
						} else {
							// Does nothing.
						}

						if (randomNumber2 > 50) {
							targetAngleBody += 90;
						} else {
							targetAngleBody -= 90;
						}

						targetAngleBody = (targetAngleBody + 360) % 360;
					} else {
						// Does nothing.
					}

					int newPosition = (int) (boundary2D.xPos * 4)
							+ (129 - (int) (boundary2D.yPos * 4)) * 80;

					// Check whether the next move will embed into obstacles

					displacement.set(0, 0, 0.01);
					displacement.rotate_XZ(targetAngleBody);
					boundary2D.update(displacement);

					boolean canMove = true;
					// Test againt type 1 & 2 obstacles
					if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
						forward = false;
						canMove = false;
					} else if (ObstacleMap.collideWithObstacle2(this,
							newPosition)) {
						forward = false;
						canMove = false;
					} else {
						displacement.scale(-1);
						boundary2D.update(displacement);
						displacement.reset();
					}
					

					if (!canMove) {
						if (unstuck) {
							ObstacleMap.giveWay(this, position);
						} else {
							// Does nothing.
						}

						// Change direction if unable to move with current
						// direction

						targetAngleBody = targetAngle;
						// Generate 2 new directions
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
						} else {
							displacement.scale(-1);
							boundary2D.update(displacement);
							displacement.reset();
							// Check if tank is able to move freely at angle 1
							displacement.set(0, 0, 0.01);
							displacement.rotate_XZ(angle2);
							boundary2D.update(displacement);
							newPosition = (int) (boundary2D.xPos * 4)
									+ (129 - (int) (boundary2D.yPos * 4)) * 80;
						}

						// Test againt type 1 & 2 obstacles
						if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
							canMoveAngle2 = false;
						} else if (ObstacleMap.collideWithObstacle2(this,
								newPosition)) {
							canMoveAngle2 = false;
						} else {
							// Does nothing.
						}
						displacement.scale(-1);
						boundary2D.update(displacement);
						displacement.reset();
						/*
						 * Move to a valid direction. if both directions are
						 * valid, then move to the direction that is closer to
						 * targetAngleBody if neither direction is valid, then
						 * update the AI status to "stucked"(which does nothing
						 * at this moment)
						 */

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
								forward = true;
							}
							

						} else {
							// Got stucked!!
							stuckCount = 10;

							// Tell surrounding units to move away
							ObstacleMap.giveWay(this, position);

						}

						if (Math.abs((previousTargetAngleBody + 180) % 360
								- targetAngleBody) <= 50) {
							targetAngleBody = previousTargetAngleBody;
						} else {
							// Does nothing.
						}

					} else {
						// Double check whether the move is valid
						displacement.set(0, 0, 0.01);
						displacement.rotate_XZ(targetAngleBody);
						boundary2D.update(displacement);
						newPosition = (int) (boundary2D.xPos * 4)
								+ (129 - (int) (boundary2D.yPos * 4)) * 80;
					}

					// Test againt type 1 & 2 obstacles
					if (ObstacleMap.collideWithObstacle1(this, newPosition)) {
						forward = false;

					} else if (ObstacleMap.collideWithObstacle2(this,
							newPosition)) {
						forward = false;

					} else {
						displacement.scale(-1);
						boundary2D.update(displacement);
						displacement.reset();
					}
				}
			}
			previousTargetAngleBody = targetAngleBody;
		}
	}
}