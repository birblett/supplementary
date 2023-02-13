package com.birblett.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MagicSand extends Block {

    // determines if the surface of the magic sand is exposed to air
    public static final BooleanProperty EXPOSED = BooleanProperty.of("exposed");
    // public static final MagicSand MAGIC_SAND = new MagicSand(FabricBlockSettings.copyOf(Blocks.SAND));

    public MagicSand(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EXPOSED, true));
    }

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

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        // plays sound on break only if exposed?
        if(state.get(EXPOSED)) {
            world.playSound(null, pos, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.AMBIENT, 1, 1);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EXPOSED);
    }
}
