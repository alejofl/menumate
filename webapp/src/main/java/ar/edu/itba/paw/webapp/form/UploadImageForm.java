package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.ValidImage;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.Size;

public class UploadImageForm {

    @ValidImage
    @FormDataParam("image")
    private FormDataBodyPart formDataBodyPart;

    @Size(max = ControllerUtils.IMAGE_MAX_SIZE, message = "{Size.UploadImageForm.bytes}")
    @FormDataParam("image")
    private byte[] bytes;

    public FormDataBodyPart getFormDataBodyPart() {
        return formDataBodyPart;
    }

    public void setFormDataBodyPart(FormDataBodyPart formDataBodyPart) {
        this.formDataBodyPart = formDataBodyPart;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
