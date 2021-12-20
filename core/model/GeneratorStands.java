package com.sweatsunited.core.model;

import com.sweatsunited.core.types.UpdatableStand;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;

@Getter @Setter
public class GeneratorStands {

    private final EntityArmorStand livingEntity;
    private UpdatableStand updatableStand;

    public GeneratorStands(EntityArmorStand livingEntity, UpdatableStand updatableStand){
        this.livingEntity = livingEntity;
        this.updatableStand = updatableStand;
    }

}