package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import model.types.Basic;
import model.types.Carnivorous;
import model.types.Monstera;
import model.types.Succulent;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Basic.class, name = "BASIC"),
        @JsonSubTypes.Type(value = Carnivorous.class, name = "CARNIVOROUS"),
        @JsonSubTypes.Type(value = Succulent.class, name = "SUCCULENT"),
        @JsonSubTypes.Type(value = Monstera.class, name = "MONSTERA")
})
public interface PlantType {
    @JsonIgnore
    double getUniqueValue();
    @JsonIgnore
    String getInfo();
}
