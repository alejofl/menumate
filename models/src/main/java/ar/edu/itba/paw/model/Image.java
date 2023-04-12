package ar.edu.itba.paw.model;

public class Image {

    private final long imageId;
    private byte[] bytes;

    public Image(long imageId, byte[] bytes) {
        this.imageId = imageId;
        this.bytes = bytes;
    }

    public long getImageId() {
        return imageId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] image) {
        this.bytes = image;
    }
}
