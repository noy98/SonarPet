package net.techcable.sonarpet.nms.versions.v1_9_R1;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.Navigation;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.nms.NMSInsentientEntity;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class NMSEntityInsentientImpl extends NMSLivingEntityImpl implements NMSInsentientEntity {

    //
    // !!!!! Highly version-dependent !!!!!
    // Check these every minor update!
    //

    private static final MethodHandle GET_DEATH_SOUND_METHOD_HANDLE = Reflection.getMethod(
            EntityLiving.class,
            "bS"
    );


    //
    // Breakage likely, check for bugs here
    //

    @Override
    public float getVerticalFaceSpeed() {
        return getHandle().N();
    }

    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    @Override
    public void clearGoals() {
        PathfinderGoalSelector goalSelector = getHandle().goalSelector;
        ImmutableList<PineappleField<PathfinderGoalSelector, Set>> fieldsToClear = PineappleField.findFieldsWithType(PathfinderGoalSelector.class, Set.class);
        if (fieldsToClear.size() != 2) {
            throw new AssertionError("Unexpected number of fields to clear: " + fieldsToClear);
        }
        for (PineappleField<PathfinderGoalSelector, Set> field : fieldsToClear) {
            field.get(goalSelector).clear();
        }
    }

    @Override
    public void lookAt(Entity other, float perTick, float verticalFaceSpeed) {
        getHandle().getControllerLook().a(((CraftEntity) other).getHandle(), perTick, verticalFaceSpeed);
    }

    @Override
    public void jump() {
        getHandle().getControllerJump().a();
    }

    @Override
    public void setCanSwim(boolean b) {
        ((Navigation) getHandle().getNavigation()).c(b);
    }

    //
    // Deobfuscated methods :)
    //

    @Override
    public void setFollowRange(double followRange) {
        getHandle().getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(followRange);
    }

    @Override
    public LivingEntity getTarget() {
        EntityLiving entity = getHandle().getGoalTarget();
        return entity != null ? (LivingEntity) entity.getBukkitEntity() : null;
    }

    @Override
    public void setTarget(LivingEntity target) {
        getHandle().setGoalTarget(((CraftLivingEntity) target).getHandle());
    }

    @Override
    public net.techcable.sonarpet.nms.Navigation getNavigation() {
        return new NavigationImpl((Navigation) getHandle().getNavigation());
    }

    //
    // Utility methods and wrappers
    //

    @Override
    public EntityInsentient getHandle() {
        return (EntityInsentient) super.getHandle();
    }

    @Override
    @SneakyThrows
    public Sound getDeathSound() {
        SoundEffect soundEffect = (SoundEffect) GET_DEATH_SOUND_METHOD_HANDLE.invoke(getHandle());
        return NMSImpl.toBukkitSound(soundEffect);
    }

    public NMSEntityInsentientImpl(EntityInsentient handle) {
        super(handle);
    }
}
