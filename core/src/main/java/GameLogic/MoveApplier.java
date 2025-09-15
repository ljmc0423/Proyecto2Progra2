package GameLogic;

public final class MoveApplier {

    public void apply(TileMap tileMap, Player player, MoveResult moveResult) {
        switch (moveResult.getType()) {
            case STEP:
                applyStep(player, moveResult);
                break;
            case PUSH:
                applyPush(tileMap, player, moveResult);
                break;
            default:
                break;
        }
    }

    private void applyStep(Player player, MoveResult moveResult) {
        player.applyStep(moveResult.getNewX(), moveResult.getNewY());
    }

    private void applyPush(TileMap tileMap, Player player, MoveResult moveResult) {
        int nx = moveResult.getNewX();
        int ny = moveResult.getNewY();
        int bx = moveResult.getBoxDestX();
        int by = moveResult.getBoxDestY();

        char previousFront = moveResult.getPreviousFront();
        char previousDest = moveResult.getPreviousDest();

        char newBoxDest = (previousDest == TileMap.TARGET) ? TileMap.BOX_ON_TARGET : TileMap.BOX;
        tileMap.setTile(bx, by, newBoxDest);

        char newBoxSource = (previousFront == TileMap.BOX_ON_TARGET) ? TileMap.TARGET : TileMap.FLOOR;
        tileMap.setTile(nx, ny, newBoxSource);

        player.applyPush(nx, ny);
    }
}
