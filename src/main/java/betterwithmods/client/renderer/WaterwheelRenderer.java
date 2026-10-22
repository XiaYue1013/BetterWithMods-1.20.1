package betterwithmods.client.renderer;

import betterwithmods.BWMod;
import betterwithmods.common.blockentity.WaterwheelBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class WaterwheelRenderer implements BlockEntityRenderer<WaterwheelBlockEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BWMod.MODID, "waterwheel"), "main");
    public static final ResourceLocation TEXTURE = new ResourceLocation(BWMod.MODID, "textures/blocks/waterwheel.png");

    private final ModelPart root;

    public WaterwheelRenderer(BlockEntityRendererProvider.Context context) {
        this.root = context.bakeLayer(LAYER_LOCATION);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition axle = root.addOrReplaceChild("axle", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -3.0F, -6.5F, 6.0F, 6.0F, 13.0F), PartPose.ZERO);

        for (int i = 0; i < 8; i++) {
            float angle = (float) Math.PI * i / 4.0F;
            axle.addOrReplaceChild("spoke_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(2.5F, -1.0F, -7.0F, 36.0F, 2.0F, 14.0F), PartPose.rotation(0.0F, 0.0F, angle));
        }

        for (int i = 0; i < 8; i++) {
            float angle = 0.79F * i;
            float x = 30.0F * (float) Math.cos(angle);
            float y = 30.0F * (float) Math.sin(angle);
            axle.addOrReplaceChild("paddle_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0F, -1.0F, -6.0F, 22.0F, 2.0F, 12.0F), PartPose.offsetAndRotation(x, y, 0.0F, 0.0F, 0.0F, 1.96F + 0.79F * i));
        }

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void render(WaterwheelBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        Direction.Axis axis = be.getAxis();
        float rotation = be.getCurrentRotation() + (be.getPreviousRotation() * partialTick);

        if (axis == Direction.Axis.X) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotation));
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));
        root.render(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
