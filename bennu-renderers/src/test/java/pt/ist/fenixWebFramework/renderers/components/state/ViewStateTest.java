package pt.ist.fenixWebFramework.renderers.components.state;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ViewStateTest {

    private ViewState viewState;
    private String serialized;

    @Before
    public void setup() throws IOException {
        this.viewState = new ViewState();
        this.viewState.setLayout("layout");
        this.serialized = ViewState.encodeToBase64(Collections.singletonList(viewState));
        System.out.println(this.serialized.split("_")[1]);
    }

    @Test
    public void ableToDeSerializeDeserialized() throws IOException, ClassNotFoundException {
        List<IViewState> collection = Collections.singletonList(viewState);
        Class<?> collectionType = collection.getClass();
        String serialized = ViewState.encodeToBase64(collection);

        List<IViewState> other = ViewState.decodeFromBase64(serialized);

        Assert.assertEquals(collectionType, other.getClass());
        Assert.assertEquals(1, other.size());

        IViewState state = other.get(0);

        Assert.assertEquals(ViewState.class, state.getClass());
        Assert.assertEquals("layout", state.getLayout());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidViewState() throws ClassNotFoundException, IOException {
        ViewState.decodeFromBase64(serialized.substring(0, serialized.indexOf("_")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsViewStateWithInvalidSignature()
            throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, IOException {
        String serializedPart = serialized.split("_")[0];
        String signed = Base64.getEncoder().encodeToString(sign(serializedPart));
        ViewState.decodeFromBase64(serializedPart + "_" + signed);
    }

    private byte[] sign(String serializedPart) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(Base64.getDecoder().decode(serializedPart.getBytes(StandardCharsets.UTF_8)));
    }

}
