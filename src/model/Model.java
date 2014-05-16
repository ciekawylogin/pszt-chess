package model;

import common.FieldMockup;
import common.GameStateMockup;
import common.Mockup;
import common.PlayerMockup;

public class Model {

	/// rozmiar planszy
	private final static int BOARD_SIZE = 8;
	
	/// po ile rzędów pionków rozstawić na starcie (dla wymiaru 8 jest to najczęściej
	/// 3, dla wymiaru 10 - 4)
	private final static int INITIAL_CHECKERS_ROWS = 3;
	
	/// odnosnik do planszy
	private Board board;
	
	/// gracze
	private Player players[];
	
	/// numer aktywnego gracze
	private int active_player;
	
	public Model() {
		this.board = new Board(Model.BOARD_SIZE, Model.INITIAL_CHECKERS_ROWS);
		this.players = new Player[2];
	}
	
	/**
	 * @return true wtedy i tylko wtedy, gdy jakikolwiek pionek jest zaznaczony
	 */
	public final boolean isAnyCheckerSelected() {
		for(int x=0; x<8; ++x)
		{
			for(int y=0; y<8; ++y)
			{
				if(board.getField(x, y).isSelected())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Przemieszcza zaznaczony pionek na pole o współrzędnych (target_x, target_y).
	 * 
	 * Możliwe warianty wykonania ruchu:
	 * + jeżeli jakiś pionek jest zaznaczony, a docelowe pole znajduje się 1 pole 
	 * 	 naprzód (po skosie) od obecnej pozycji pionka (zwykły ruch), to pionek 
	 *   przesuwa się na zadane pole, następuje zmiana kolejki, a pionek zostaje 
	 *   odznaczony; UWAGA: zwykły ruch jest dozwolony tylko wtedy, gdy gracz nie
	 *   ma żadnego bicia
	 * + jeżeli jakiś pionek jest zaznaczony, a docelowe pole znajduje się 2 pola 
	 *   naprzód od niego i między polem docelowym a obecnym znajduje się pionek 
	 *   przeciwnika (bicie), wówczas pionek przesuwa się 2 na pole docelowe, a pionek 
	 *   pzreciwnika zostaje usunięty dodatkowo przy biciu funkcja musi sprawdzić, 
	 *   czy jest możliwe kolejne bicie; 
	 *   jeżeli nie, to:
	 * ++ następuje zmiana kolejki
	 * ++ pionek zostaje odznaczony
	 *   jeżeli tak, to:
	 * ++ nie następuje zmiana kolejki
	 * ++ pionek nie zostaje odznaczony
	 * ++ w następnej kolejce pionek zostaje zablokowany, tj. gracz nie może go odznaczyć,
	 *    musi wykonać bicie tym pionkiem
	 * + jeżeli nie zachodzi żaden z powyższych przypadków, to ruch jest uznawany 
	 *   za niepoprawny; funkcja rzuca wyjątek, nie zostają wprowadzone żadne zmiany 
	 *   w modelu
	 * 
	 * @param target_x współrzędna x docelowego pola
	 * @param target_y współrzędna y docelowego pola
	 * @return true jeżeli ruch jest dozwolony i został wykonany
	 * 		   false jeżeli ruch jest niedozwolony; w takim wypadku żadne zmiany nie zostają
	 * 		   wprowadzone do modelu
	 */
	public final boolean moveSelectedCheckerTo(int target_x, int target_y) {
		Coordinate source_coordinate = getSelectedCheckerCoordinate();
		int source_x = source_coordinate.getX();
		int source_y = source_coordinate.getY();
		CheckerType checkerType = board.getField(source_x, source_y).getChecker().getType();
		if(checkerType == CheckerType.QUEEN) {
			return makeQueenMove(source_x, source_y, target_x, target_y);
		} else {
			return makeNormalCheckerMove(source_x, source_y, target_x, target_y);
		}
	}
	

	private boolean makeNormalCheckerMove(int source_x, int source_y, int target_x, int target_y) {

		if(isNormalCheckerMoveCorrect(source_x, source_y, target_x, target_y)) {
			Field oldField = board.getField(source_x, source_y);
			Checker checker = oldField.getChecker();
			oldField.removeChecker();

			Field newField = board.getField(target_x, target_y);
			newField.setChecker(checker);
			checker.setPositionOnBoard(target_x, target_y);
			
			return true;
			
		} else if(isCheckerCaptureMoveCorrect(source_x, source_y, target_x, target_y)) {
			int checkerToRemoveX = (target_x + source_x) / 2;
			int checkerToRemoveY = (target_y + source_y) / 2; 
			
			board.getField(checkerToRemoveX, checkerToRemoveY).removeChecker();
			
			return true;
			
		}
			
		return false;
	}

	private boolean isCheckerCaptureMoveCorrect(int source_x, int source_y, int target_x, int target_y) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isNormalCheckerMoveCorrect(int source_x, int source_y, int target_x, int target_y) {
		Checker checker = board.getField(source_x, source_y).getChecker();
		return true;
	}

	private boolean makeQueenMove(int source_x, int source_y, int target_x, int target_y) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Znajduje zaznaczony pionek
	 */
	public final Coordinate getSelectedCheckerCoordinate() {
		for(int x=0; x<8; ++x)
		{
			for(int y=0; y<8; ++y)
			{
				if(board.getField(x, y).isSelected())
				{
					return new Coordinate(x, y);
				}
			}
		}
		throw new RuntimeException("hm");
	}
	
	/**
	 * Odznacza zaznaczonego obecnie pionka. Jeżeli odznaczenie jest niedozwolone
	 * (taka sytuacja zachodzi gdy gracz ma wymuszone bicie), to rzuca wyjątek
	 * 
	 * @throws RuntimeException jeżeli gracz ma wymuszone bicie
	 * @throws RuntimeException jeżeli żaden pionek nie jest zaznaczony
	 */
	public final void unselectChecker() {
		for(int x=0; x<8; ++x)
		{
			for(int y=0; y<8; ++y)
			{
				if(board.getField(x, y).isSelected())
				{
					board.getField(x, y).unselect();
					return;
				}
			}
		}
		throw new RuntimeException("unselect requested, but no checker was selected");
	}

	/**
	 * Sprawdza, czy na danej pozycji znajduje się zaznaczony pionek.
	 * @param x współrzędna x pozycji do sprawdzenia
	 * @param y współrzędna y pozycji do sprawdzenia
	 * @return true jeżelili na danej pozycji znajduje się pionek i jest on zaznaczony
	 * 		   false jeżeli na danej pozycji nie ma pionka lub pionek jest niezaznaczony
	 */
	public final boolean isCheckerSelected(int x, int y) {
		Field field = board.getField(x, y);
		return field.isSelected();
	}

	/**
	 * Sprawdza, czy na zadanej pozycji znajduje się pionek aktywnego gracza 
	 * (gracz jest aktywny = 'jest jego ruch').
	 * 
	 * @param x współrzędna x pozycji do sprawdzenia
	 * @param y współrzędna y pozycji do sprawdzenia
	 * @return true jeżeli na polu (x, y) znajduje się pionek aktywnego gracza.
	 */
	public final boolean isCurrentPlayerCheckerOnPosition(int x, int y) {
		Checker checker = board.getField(x, y).getChecker();
		return checker != null && (
			   (checker.getColor().isWhite() && active_player == 0) ||
			   (checker.getColor().isBlack() && active_player == 1));
	}

	/**
	 * Zaznacza pionek na pozycji (x, y).
	 * 
	 * @param x współrzędna x pionka do zaznaczenia
	 * @param y współrzędna y pionka do zaznaczenia
	 * 
	 * @throws RuntimeException jeżeli na pozycji (x, y) nie ma pionka aktywnego gracza
	 * @throws RuntimeException jeżeli jakiś pionek jest już zaznaczony
	 */
	public final void selectChecker(int x, int y) {
		board.getField(x, y).select();
	}

	/**
	 * Rozpoczyna nową grę, tj. tworzy planszę, ustawia pionki i ustawia białego gracza
	 * jako aktywnego.
	 */
	public final void startGame() {
		// rozstaw pionki
		board.setUp();
		
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException("Not yet implemented");
		
	}

	/**
	 * Generuje makietę
	 * @return makieta obecnego stanu gry
	 * @see #Mockup
	 */
	public final Mockup getMockup() {
		Mockup mockup = new Mockup(); // wypelnij mnie
		mockup.setGameState(GameStateMockup.PLAYER_1_MOVE);
		for(int i=0;i<8;++i)
			for(int j=0;j<8;++j)
				mockup.setField(board.getField(i, j).getMockup(), i, j);
		mockup.setPlayers(PlayerMockup.HUMAN_PLAYER, 0);
		mockup.setPlayers(PlayerMockup.AI_PLAYER, 1);
		return mockup;
		
	}

	/**
	 * Sprawdza, czy gracz 1 wygrał
	 * @return true wtedy i tylko wtedy, gdy spelnione są oba poniższe warunki:
	 * 	+ Gracz 2 jest aktywny
	 *  + Gracz 2 nie ma żadnego dozwolonego ruchu
	 */
	public boolean hasPlayer1Won() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Sprawdza, czy gracz 2 wygrał
	 * @return true wtedy i tylko wtedy, gdy spelnione są oba poniższe warunki:
	 * 	+ Gracz 1 jest aktywny
	 *  + Gracz 1 nie ma żadnego dozwolonego ruchu
	 *  
	 *  @TODO copy-paste programming - moze przedefiniowac interfejs?
	 */
	public boolean hasPlayer2Won() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Zwraca rozmiar planszy.
	 * @return BOARD_SIZE
	 */
	public static int getBoardSize() {
		return BOARD_SIZE;
	}

	
}
 
