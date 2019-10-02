package com.dungeon.engine.controller;

public class ControllerConfig {
	private final int povControl;
	private final int aimAxisX;
	private final int aimAxisY;
	private final int moveAxisX;
	private final int moveAxisY;
	private final int buttonA;
	private final int buttonB;
	private final int buttonX;
	private final int buttonY;
	private final int buttonL1;
	private final int buttonL2;
	private final int buttonR1;
	private final int buttonR2;
	private final boolean aimAxisXInvert;
	private final boolean aimAxisYInvert;
	private final boolean moveAxisXInvert;
	private final boolean moveAxisYInvert;

	private ControllerConfig(Builder builder) {
		povControl = builder.povControl;
		aimAxisX = builder.aimAxisX;
		aimAxisY = builder.aimAxisY;
		moveAxisX = builder.moveAxisX;
		moveAxisY = builder.moveAxisY;
		buttonA = builder.buttonA;
		buttonB = builder.buttonB;
		buttonX = builder.buttonX;
		buttonY = builder.buttonY;
		buttonL1 = builder.buttonL1;
		buttonL2 = builder.buttonL2;
		buttonR1 = builder.buttonR1;
		buttonR2 = builder.buttonR2;
		aimAxisXInvert = builder.aimAxisXInvert;
		aimAxisYInvert = builder.aimAxisYInvert;
		moveAxisXInvert = builder.moveAxisXInvert;
		moveAxisYInvert = builder.moveAxisYInvert;
	}

	public int getPovControl() {
		return povControl;
	}

	public int getAimAxisX() {
		return aimAxisX;
	}

	public int getAimAxisY() {
		return aimAxisY;
	}

	public int getMoveAxisX() {
		return moveAxisX;
	}

	public int getMoveAxisY() {
		return moveAxisY;
	}

	public int getButtonA() {
		return buttonA;
	}

	public int getButtonB() {
		return buttonB;
	}

	public int getButtonX() {
		return buttonX;
	}

	public int getButtonY() {
		return buttonY;
	}

	public int getButtonL1() {
		return buttonL1;
	}

	public int getButtonL2() {
		return buttonL2;
	}

	public int getButtonR1() {
		return buttonR1;
	}

	public int getButtonR2() {
		return buttonR2;
	}

	public boolean getAimAxisXInvert() {
		return aimAxisXInvert;
	}

	public boolean getAimAxisYInvert() {
		return aimAxisYInvert;
	}

	public boolean getMoveAxisXInvert() {
		return moveAxisXInvert;
	}

	public boolean getMoveAxisYInvert() {
		return moveAxisYInvert;
	}

	public static final class Builder {
		private int povControl;
		private int aimAxisX;
		private int aimAxisY;
		private int moveAxisX;
		private int moveAxisY;
		private int buttonA;
		private int buttonB;
		private int buttonX;
		private int buttonY;
		private int buttonL1;
		private int buttonL2;
		private int buttonR1;
		private int buttonR2;
		private boolean moveAxisXInvert;
		private boolean moveAxisYInvert;
		private boolean aimAxisXInvert;
		private boolean aimAxisYInvert;

		public Builder() {
		}

		public Builder(ControllerConfig copy) {
			this.povControl = copy.getPovControl();
			this.moveAxisY = copy.getMoveAxisY();
			this.moveAxisX = copy.getMoveAxisX();
			this.aimAxisX = copy.getAimAxisX();
			this.aimAxisY = copy.getAimAxisY();
			this.buttonA = copy.getButtonA();
			this.buttonB = copy.getButtonB();
			this.buttonX = copy.getButtonX();
			this.buttonY = copy.getButtonY();
			this.buttonL1 = copy.getButtonL1();
			this.buttonL2 = copy.getButtonL2();
			this.buttonR1 = copy.getButtonR1();
			this.buttonR2 = copy.getButtonR2();
			this.aimAxisXInvert = copy.getAimAxisXInvert();
			this.aimAxisXInvert = copy.getAimAxisYInvert();
			this.aimAxisXInvert = copy.getAimAxisXInvert();
			this.aimAxisXInvert = copy.getAimAxisYInvert();
		}

		public Builder povControl(int povControl) {
			this.povControl = povControl;
			return this;
		}

		public Builder aimAxisX(int aimAxisX) {
			this.aimAxisX = aimAxisX;
			return this;
		}

		public Builder aimAxisY(int aimAxisY) {
			this.aimAxisY = aimAxisY;
			return this;
		}

		public Builder moveAxisY(int moveAxisY) {
			this.moveAxisY = moveAxisY;
			return this;
		}

		public Builder moveAxisX(int moveAxisX) {
			this.moveAxisX = moveAxisX;
			return this;
		}

		public Builder buttonA(int buttonA) {
			this.buttonA = buttonA;
			return this;
		}

		public Builder buttonB(int buttonB) {
			this.buttonB = buttonB;
			return this;
		}

		public Builder buttonX(int buttonX) {
			this.buttonX = buttonX;
			return this;
		}

		public Builder buttonY(int buttonY) {
			this.buttonY = buttonY;
			return this;
		}

		public Builder buttonL1(int buttonL1) {
			this.buttonL1 = buttonL1;
			return this;
		}

		public Builder buttonL2(int buttonL2) {
			this.buttonL2 = buttonL2;
			return this;
		}

		public Builder buttonR1(int buttonR1) {
			this.buttonR1 = buttonR1;
			return this;
		}

		public Builder buttonR2(int buttonR2) {
			this.buttonR2 = buttonR2;
			return this;
		}

		public Builder aimAxisXInvert(boolean aimAxisXInvert) {
			this.aimAxisXInvert = aimAxisXInvert;
			return this;
		}

		public Builder aimAxisYInvert(boolean aimAxisYInvert) {
			this.aimAxisYInvert = aimAxisYInvert;
			return this;
		}

		public Builder moveAxisXInvert(boolean moveAxisXInvert) {
			this.moveAxisXInvert = moveAxisXInvert;
			return this;
		}

		public Builder moveAxisYInvert(boolean moveAxisYInvert) {
			this.moveAxisYInvert = moveAxisYInvert;
			return this;
		}

		public ControllerConfig build() {
			return new ControllerConfig(this);
		}
	}

}
