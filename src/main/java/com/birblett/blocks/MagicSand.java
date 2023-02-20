package com.birblett.blocks;

import com.birblett.entities.damage_sources.MagicSandDamage;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static com.birblett.registry.SupplementaryEntities.MAGIC_SAND_DAMAGE;

public class MagicSand extends Block {

    // determines if the surface of the magic sand is exposed to air
    public static final BooleanProperty EXPOSED = BooleanProperty.of("exposed");

    public MagicSand(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EXPOSED, true));
    }

    /*
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            player.sendMessage(Text.of("ooga booga"), false);
        }

        // flips state on use
        world.setBlockState(pos, state.with(EXPOSED, !state.get(EXPOSED)));
        player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);

        return ActionResult.SUCCESS;
    }
    */

    /*
    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        // plays sound on break only if exposed
        if(state.get(EXPOSED)) {
            world.playSound(null, pos, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.AMBIENT, 1, 1);
        }
    }
     */

    // this is called when a neighbor gets a block update
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // if the block above is air, return a true exposed state
        if (world.getBlockState(pos.up()).isAir()) {
            return super.getStateForNeighborUpdate(state.with(EXPOSED, true), direction, neighborState, world, pos, neighborPos);
        }
        // otherwise return a false exposed state
        else {
            return super.getStateForNeighborUpdate(state.with(EXPOSED, false), direction, neighborState, world, pos, neighborPos);
        }
    }

    // quicksand logic
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.get(EXPOSED)) {
            entity.slowMovement(state, new Vec3d(
                    Math.max(0.25 + (0.75 * (entity.getY() - pos.getY())), 0.25),
                    Math.max(0.10 + (0.90 * (entity.getY() - pos.getY())), 0.10),
                    Math.max(0.25 + (0.75 * (entity.getY() - pos.getY())), 0.25)));
        }
        else {
            if (entity.getEyePos().getY() < pos.getY() + 1) {
                entity.damage(MAGIC_SAND_DAMAGE, 2);
            }
            entity.slowMovement(state, new Vec3d(0.25, 0.1, 0.25));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EXPOSED);
    }
}
