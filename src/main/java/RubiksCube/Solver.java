package RubiksCube;

import java.awt.*;
import java.util.Stack;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Solver extends Stack<Move> implements Observer<Move>, CubeValues, CubeColors {

    //<editor-fold defaultstate="collapsed" desc="Attributes">
    private Cube cube;
    private Face upFace , leftFace, frontFace, rightFace, backFace, downFace;
    private boolean computerSolving, reshuffling;
    private Stack<Move> computerMoveStack, solveStack;
    private EdgesMap edgesMap;
    private CornersMap cornersMap;
    private Color upColor, leftColor, frontColor, rightColor, backColor, downColor;
    private DirectionLabel directionLabel;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public Solver(Cube cube, DirectionLabel directionLabel) {
        this.cube = cube;
        this.directionLabel = directionLabel;
        upFace = cube.getUpFace();
        leftFace = cube.getLeftFace();
        frontFace = cube.getFrontFace();
        rightFace = cube.getRightFace();
        backFace = cube.getBackFace();
        downFace = cube.getDownFace();
        upColor = UP_FACE_COLOR;
        leftColor = LEFT_FACE_COLOR;
        frontColor = FRONT_FACE_COLOR;
        rightColor = RIGHT_FACE_COLOR;
        backColor = BACK_FACE_COLOR;
        downColor = DOWN_FACE_COLOR;
        computerSolving = false;
        reshuffling = false;
        edgesMap = new EdgesMap(upFace, leftFace, frontFace, rightFace, backFace, downFace);
        cornersMap = new CornersMap(upFace, leftFace, frontFace, rightFace, backFace, downFace);
        solveStack = new Stack<>();
        computerMoveStack = new Stack<>();
        Move.setCounterMoves();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Observer methods">
    @Override
    public void onSubscribe(Disposable disposable) {

    }

    @Override
    public void onNext(Move move) {
        cube.repaint();
        if (move.equals(Move.SHUFFLE)) {
            computerMoveStack.clear();
            solveStack.clear();
        }
        else if (computerSolving) {
            computerMoveStack.push(move);
        } else if (!reshuffling) {
            if (!solveStack.isEmpty()) {
                Move nextMove = solveStack.peek();
                if (move.equals(nextMove)) {
                    solveStack.pop();
                } else {
                    solveStack.push(move.getCounterMove());
                }
                if (solveStack.isEmpty()) {
                    directionLabel.setText("Good job!");
                } else {
                    directionLabel.setText(solveStack.peek().getPrompt());
                }
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Solve">
    public void solve() {
        computerSolving = true;
        solveTopLayer();
        // solveMiddleLayer();
        //solveBottomLayer();
        computerSolving = false;
        reshuffleCube();
    }

    private void reshuffleCube() {
        reshuffling = true;

        while(!computerMoveStack.isEmpty()) {
            Move move = computerMoveStack.pop();
            cube.doMove(move.getCounterMove());
            solveStack.push(move);
        }
        directionLabel.setText(solveStack.peek().getPrompt());

        reshuffling = false;
    }

    //<editor-fold defaultstate-"collapsed" desc="Solve top layer">
    private void solveTopLayer() {
        positionWhiteCenterSquare();
        createWhiteCross();
        // putWhiteCornersInPlace();
    }

    //<editor-fold defaultstate="collapsed" desc="Position white center square">
    private void positionWhiteCenterSquare() {
        if (!squareIsColor(upFace.squares[MIDDLE_ROW][MIDDLE_COLUMN], upColor)) {
            if (squareIsColor(leftFace.squares[MIDDLE_ROW][MIDDLE_COLUMN], upColor)) {
                cube.turnCubeClockwiseAlongZAxis();
            } else if (squareIsColor(frontFace.squares[MIDDLE_ROW][MIDDLE_COLUMN], upColor)) {
                cube.turnCubeUp();
            } else if (squareIsColor(rightFace.squares[MIDDLE_ROW][MIDDLE_COLUMN], upColor)) {
                cube.turnCubeCounterclockwiseAlongZAxis();
            } else if (squareIsColor(backFace.squares[MIDDLE_ROW][MIDDLE_COLUMN], upColor)) {
                cube.turnCubeDown();
            } else {
                cube.doubleVerticalCubeTurn();
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create white cross">
    private void createWhiteCross() {
        System.out.println(edgesAreOriented(upFace, upColor));
//        while (!whiteEdgesAreOriented()) {
//            orientUpFaceWhiteEdges();
//            moveWhiteEdgeSquaresFromDownFaceToUpFace();
//            moveAllWhiteEdgeSquaresFromBottomLayerToUpFace();
//            moveWhiteEdgeSquaresFromMiddleLayerToUpFace();
//            moveWhiteEdgeSquaresFromTopLayerToUpFace();
//        }
    }

    private boolean edgesAreOriented(Face face, Color color) {
        return crossExists(face, color) && adjacentEdgesAreOriented(face);
    }

    //</editor-fold>
    //</editor-fold>

    private boolean adjacentEdgesAreOriented(Face face) {
        Color topEdgeColor = getAdjacentEdgeColor(face.squares[TOP_ROW][MIDDLE_COLUMN]);
        Color bottomEdgeColor = getAdjacentEdgeColor(face.squares[BOTTOM_ROW][MIDDLE_COLUMN]);
        Color leftEdgeColor = getAdjacentEdgeColor(face.squares[MIDDLE_ROW][LEFT_COLUMN]);
        Color rightEdgeColor = getAdjacentEdgeColor(face.squares[MIDDLE_ROW][RIGHT_COLUMN]);

        if (face.equals(upFace)) {
            if (bottomEdgeColor.equals(frontColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, rightColor, backColor, leftColor);
            } else if (bottomEdgeColor.equals(leftColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, frontColor, rightColor, backColor);
            } else if (bottomEdgeColor.equals(rightColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, backColor, leftColor, frontColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, leftColor, frontColor, rightColor);
            }
        } else if (face.equals(leftFace)) {
            if (bottomEdgeColor.equals(downColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, frontColor, upColor, backColor);
            } else if (bottomEdgeColor.equals(frontColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, upColor, backColor, downColor);
            } else if (bottomEdgeColor.equals(upColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, backColor, downColor, frontColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, downColor, frontColor, upColor);
            }
        } else if (face.equals(frontFace)) {
            if (bottomEdgeColor.equals(downColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, rightColor, upColor, leftColor);
            } else if (bottomEdgeColor.equals(rightColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, upColor, leftEdgeColor, downColor);
            } else if (bottomEdgeColor.equals(leftColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, downColor, rightColor, upColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, leftColor, downColor, rightColor);
            }
        } else if (face.equals(rightFace)) {
            if (bottomEdgeColor.equals(upColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, frontColor, downColor, backColor);
            } else if (bottomEdgeColor.equals(downColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, backColor, upColor, frontColor);
            } else if (bottomEdgeColor.equals(frontColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, downColor, backColor, upColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, upColor, frontColor, downColor);
            }
        } else if (face.equals(backFace)) {
            if (bottomEdgeColor.equals(downColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, leftColor, upColor, rightColor);
            } else if (bottomEdgeColor.equals(upColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, rightColor, downColor, leftColor);
            } else if (bottomEdgeColor.equals(leftColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, upColor, rightColor, downColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, downColor, leftColor, upColor);
            }
        } else {
            if (bottomEdgeColor.equals(rightColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, frontColor, leftColor, backColor);
            } else if (bottomEdgeColor.equals(leftColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, backColor, rightColor, frontColor);
            } else if (bottomEdgeColor.equals(frontColor)) {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, leftColor, backColor, rightColor);
            } else {
                return edgesAreColored(rightEdgeColor, topEdgeColor, leftEdgeColor, rightColor, frontColor, leftColor);
            }
        }
    }

    private boolean edgesAreColored(Color rightEdge, Color topEdge, Color leftEdge,
                                    Color rightColor, Color topColor, Color leftColor) {
        return rightEdge.equals(rightColor)
                && topEdge.equals(topColor) && leftEdge.equals(leftColor);
    }

    private boolean squareIsColor(Square square, Color color) {
        return square.getColor().equals(color);
    }

    private boolean crossExists(Face face, Color color) {
        return squareIsColor(face.squares[TOP_ROW][MIDDLE_COLUMN], color)
                && squareIsColor(face.squares[MIDDLE_ROW][LEFT_COLUMN], color)
                && squareIsColor(face.squares[MIDDLE_ROW][RIGHT_COLUMN], color)
                && squareIsColor(face.squares[BOTTOM_ROW][MIDDLE_COLUMN], color);
    }

    private boolean edgeIsColor(Square square, Color color) {
        return getAdjacentEdgeColor(square).equals(color);
    }

    private Color getAdjacentEdgeColor(Square square) {
        return edgesMap.get(square).getColor();
    }
}
