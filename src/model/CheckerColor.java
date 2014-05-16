package model;

public enum CheckerColor {
	WHITE,
	BLACK;
	
	static boolean isWhite(final CheckerColor color) {
		if(color.equals(CheckerColor.WHITE)) {
			return true;
		} else {
			return false;
		}
	}
	
	static boolean isBlack(final CheckerColor color) {
		if(color.equals(CheckerColor.BLACK)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isWhite() {
		return CheckerColor.isWhite(this);
	}
	
	public boolean isBlack() {
		return CheckerColor.isBlack(this);
	}
	
	public static CheckerColor getOppositeColor(final CheckerColor color) {
		return color == CheckerColor.WHITE ? CheckerColor.BLACK : CheckerColor.WHITE;
	}
}
