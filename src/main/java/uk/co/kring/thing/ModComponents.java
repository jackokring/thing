package uk.co.kring.thing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModComponents {
    public record BaseDataComponent(int version) {

    }

    public static final Codec<BaseDataComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                // base version optional
                Codec.INT.optionalFieldOf("version", 0).forGetter(BaseDataComponent::version)
                //Codec.FLOAT.fieldOf("temperature").forGetter(MyCustomComponent::temperature),
                //Codec.BOOL.optionalFieldOf("burnt", false).forGetter(MyCustomComponent::burnt)
        ).apply(builder, BaseDataComponent::new);
    });

    static final DataComponentType<BaseDataComponent> BASE_COMPONENT_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Thing.identify("base_component"),
            DataComponentType.<BaseDataComponent>builder().persistent(CODEC).build()
    );

    protected static void initialize() {

    }
}
