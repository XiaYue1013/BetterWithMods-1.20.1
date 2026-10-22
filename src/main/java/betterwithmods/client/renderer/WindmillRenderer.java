package betterwithmods.client.renderer;

import betterwithmods.BWMod;
import betterwithmods.common.blockentity.WindmillBlockEntity;
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
import net.minecraft.world.item.DyeColor;

public class WindmillRenderer implements BlockEntityRenderer<WindmillBlockEntity> {
    public static final ModelLayerLocation HORIZONTAL_LAYER = new ModelLayerLocation(new ResourceLocation(BWMod.MODID, "windmill_horizontal"), "main");
    public static final ModelLayerLocation VERTICAL_LAYER = new ModelLayerLocation(new ResourceLocation(BWMod.MODID, "windmill_vertical"), "main");

    public static final ResourceLocation HORIZONTAL_WINDMILL = new ResourceLocation(BWMod.MODID, "textures/blocks/horizontal_windmill.png");
    public static final ResourceLocation HORIZONTAL_SAIL = new ResourceLocation(BWMod.MODID, "textures/blocks/horizontal_windmill_sail.png");

    public static final ResourceLocation VERTICAL_SHAFTS = new ResourceLocation(BWMod.MODID, "textures/blocks/vertical_windmill_shaft.png");
    public static final ResourceLocation VERTICAL_FRAME = new ResourceLocation(BWMod.MODID, "textures/blocks/vertical_windmill.png");
    public static final ResourceLocation VERTICAL_SAIL = new ResourceLocation(BWMod.MODID, "textures/blocks/vertical_windmill_sail.png");

    private final ModelPart horizontalRoot;
    private final ModelPart[] horizontalSails = new ModelPart[4];
    private final ModelPart horizontalShafts;

    private final ModelPart verticalRoot;
    private final ModelPart verticalShafts;
    private final ModelPart verticalFrame;
    private final ModelPart[] verticalSails = new ModelPart[8];

    public WindmillRenderer(BlockEntityRendererProvider.Context context) {
        this.horizontalRoot = context.bakeLayer(HORIZONTAL_LAYER);
        this.horizontalShafts = horizontalRoot.getChild("shafts");
        for (int i = 0; i < 4; i++) {
            this.horizontalSails[i] = horizontalRoot.getChild("sail_" + i);
        }

        this.verticalRoot = context.bakeLayer(VERTICAL_LAYER);
        this.verticalShafts = verticalRoot.getChild("shafts");
        this.verticalFrame = verticalRoot.getChild("frame");
        for (int i = 0; i < 8; i++) {
            this.verticalSails[i] = verticalRoot.getChild("sail_" + i);
        }
    }

    public static LayerDefinition createHorizontalLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition shafts = root.addOrReplaceChild("shafts", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.ZERO);

        for (int i = 0; i < 4; i++) {
            float angle = (float) Math.PI * i / 2.0F;
            shafts.addOrReplaceChild("shaft_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.5F, -2.0F, -2.0F, 99.0F, 4.0F, 4.0F), PartPose.rotation(0.0F, 0.0F, angle));
        }

        for (int i = 0; i < 4; i++) {
            float angle = (float) Math.PI * i / 2.0F;
            root.addOrReplaceChild("sail_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(15.0F, 1.75F, 1.0F, 84.0F, 16.0F, 1.0F), PartPose.rotation(-0.26F, 0.0F, angle));
        }

        return LayerDefinition.create(mesh, 16, 16);
    }

    public static LayerDefinition createVerticalLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition shafts = root.addOrReplaceChild("shafts", CubeListBuilder.create(), PartPose.ZERO);
        for (int i = 0; i < 8; i++) {
            float angle = 6.3F * i / 8.0F;
            shafts.addOrReplaceChild("shaft_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(68.4F, -54.0F, -2.0F, 4.0F, 108.0F, 4.0F), PartPose.rotation(0.0F, angle, 0.0F));
        }

        PartDefinition frame = root.addOrReplaceChild("frame", CubeListBuilder.create(), PartPose.ZERO);
        for (int i = 0; i < 8; i++) {
            float angle = 6.3F * i / 8.0F;
            frame.addOrReplaceChild("top_spoke_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(2.0F, -52.9F, -1.0F, 67.0F, 2.0F, 2.0F), PartPose.rotation(0.0F, angle, 0.0F));
            frame.addOrReplaceChild("bottom_spoke_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(2.0F, 50.9F, -1.0F, 67.0F, 2.0F, 2.0F), PartPose.rotation(0.0F, angle, 0.0F));

            frame.addOrReplaceChild("top_cross_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(64.9F, -52.9F, -26.0F, 2.0F, 2.0F, 52.0F), PartPose.rotation(0.0F, angle + 0.39F, 0.0F));
            frame.addOrReplaceChild("bottom_cross_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(64.9F, 50.9F, -26.0F, 2.0F, 2.0F, 52.0F), PartPose.rotation(0.0F, angle + 0.39F, 0.0F));
        }

        for (int i = 0; i < 8; i++) {
            float angle = 6.3F * i / 8.0F;
            root.addOrReplaceChild("sail_" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(70.4F, -50.0F, -20.0F, 1.0F, 100.0F, 20.0F), PartPose.rotation(0.0F, angle, 0.0F));
        }

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void render(WindmillBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        float rotation = be.getCurrentRotation() + (be.getPreviousRotation() * partialTick);

        if (be.isVertical()) {
            renderVertical(be, rotation, poseStack, bufferSource, packedLight, packedOverlay);
        } else {
            renderHorizontal(be, rotation, poseStack, bufferSource, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderHorizontal(WindmillBlockEntity be, float rotation, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction.Axis axis = be.getAxis();

        if (axis == Direction.Axis.X) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotation));
        } else {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotation));
        }

        VertexConsumer shaftConsumer = bufferSource.getBuffer(RenderType.entitySolid(HORIZONTAL_WINDMILL));
        horizontalShafts.render(poseStack, shaftConsumer, packedLight, packedOverlay);

        VertexConsumer sailConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(HORIZONTAL_SAIL));
        for (int i = 0; i < 4; i++) {
            int colorId = be.getBladeColor(i);
            DyeColor dye = DyeColor.byId(colorId);
            float[] rgb = dye.getTextureDiffuseColors();
            horizontalSails[i].render(poseStack, sailConsumer, packedLight, packedOverlay, rgb[0], rgb[1], rgb[2], 1.0F);
        }
    }

    private void renderVertical(WindmillBlockEntity be, float rotation, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));

        VertexConsumer shaftConsumer = bufferSource.getBuffer(RenderType.entitySolid(VERTICAL_SHAFTS));
        verticalShafts.render(poseStack, shaftConsumer, packedLight, packedOverlay);

        VertexConsumer frameConsumer = bufferSource.getBuffer(RenderType.entitySolid(VERTICAL_FRAME));
        verticalFrame.render(poseStack, frameConsumer, packedLight, packedOverlay);

        VertexConsumer sailConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VERTICAL_SAIL));
        for (int i = 0; i < 8; i++) {
            int colorId = be.getBladeColor(i);
            DyeColor dye = DyeColor.byId(colorId);
            float[] rgb = dye.getTextureDiffuseColors();
            verticalSails[i].render(poseStack, sailConsumer, packedLight, packedOverlay, rgb[0], rgb[1], rgb[2], 1.0F);
        }
    }
}
