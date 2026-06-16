import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
