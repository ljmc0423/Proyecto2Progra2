package GameLogic;

public class MoveLogic {

    public MoveResult applyMovement(TileMap tileMap, Position player, Directions direction) {
        int dx = direction.dx;
        int dy = direction.dy;
        int nx =  player.getX() + dx;
        int ny = player.getY() + dy;

        if (!tileMap.isInBounds(nx, ny)) {
            return MoveResult.blocked();
        }

        char front = tileMap.getTile(nx, ny);
        
        if (tileMap.isWall(front)) {
            return MoveResult.blocked();
        }

        if (tileMap.isBox(front)) {
            int bx = nx + dx;
            int by = ny + dy;
            
            if (!tileMap.isInBounds(bx, by)) {
                return MoveResult.blocked();
            }
            
            char destinyTile = tileMap.getTile(bx, by);
            if (!tileMap.isFree(destinyTile)) {
                return MoveResult.blocked();
            }
            return MoveResult.push(nx, ny, bx, by, front, destinyTile);
        }

        if (tileMap.isFree(front)) {
            return MoveResult.step(nx, ny);
        }

        return MoveResult.blocked();
    }
}
