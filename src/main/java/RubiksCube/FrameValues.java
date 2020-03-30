package RubiksCube;

public interface FrameValues {
    int FRAME_WIDTH = 750;
    int FRAME_HEIGHT = 1334;
    int NUM_FACES = 6;
    int FACE_WIDTH = 100;
    int FACE_MARGIN = 1;
    int FRAME_MARGIN = (FRAME_WIDTH - (NUM_FACES * FACE_WIDTH)) / 2;
}