package ar.edu.itba.paw.persistence.constants;

public final class ImageConstants {

    public static final byte[] NON_EXISTING_IMAGE_INFO = {4, 5, 6};
    public static final long EXISTING_IMAGE_ID = 623;
    public static final long NON_EXISTING_IMAGE_ID = 1000;
    public static final byte[] EXISTING_IMAGE_INFO = {33, 18, 86};

    private ImageConstants() {
        throw new AssertionError();
    }
}
