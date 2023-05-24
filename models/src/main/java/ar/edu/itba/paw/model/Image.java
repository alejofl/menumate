package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_image_id_seq")
    @SequenceGenerator(sequenceName = "images_image_id_seq", name = "images_image_id_seq", allocationSize = 1)
    @Column(name = "image_id")
    private Long imageId;

    @Column
    private byte[] bytes;

    Image() {

    }

    public Image(Long imageId, byte[] bytes) {
        this.imageId = imageId;
        this.bytes = bytes;
    }

    public Long getImageId() {
        return imageId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
