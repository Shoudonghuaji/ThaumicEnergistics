package thaumicenergistics.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import thaumicenergistics.registries.Renderers;
import thaumicenergistics.tileentities.TileProviderBase;
import appeng.api.util.AEColor;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockProviderBase
	implements ISimpleBlockRenderingHandler
{

	/**
	 * Side/Face array cache.
	 */
	private static ForgeDirection[] FACES = ForgeDirection.VALID_DIRECTIONS;

	/**
	 * Textures
	 */
	private IIcon baseTexture, overlayTexture;

	public RenderBlockProviderBase( final IIcon baseTexture, final IIcon overlayTexture )
	{
		// Set the textures
		this.baseTexture = baseTexture;
		this.overlayTexture = overlayTexture;
	}

	private void renderFaces( final IBlockAccess world, final int x, final int y, final int z, final IIcon texture, final boolean getFaceBrightness )
	{
		Tessellator tessellator = Tessellator.instance;

		// Get the UV's
		double minU = texture.getMinU();
		double maxU = texture.getMaxU();
		double minV = texture.getMinV();
		double maxV = texture.getMaxV();

		// Vertex +1 offsets
		int x1 = x + 1, y1 = y + 1, z1 = z + 1;

		for( ForgeDirection face : FACES )
		{
			// Should the face brightness be calculated?
			if( getFaceBrightness )
			{
				tessellator.setBrightness( world.getLightBrightnessForSkyBlocks( x + face.offsetX, y + face.offsetY, z + face.offsetZ, 0 ) );
			}

			switch ( face )
			{
				case DOWN:
					tessellator.addVertexWithUV( x, y, z, minU, maxV );
					tessellator.addVertexWithUV( x1, y, z, maxU, maxV );
					tessellator.addVertexWithUV( x1, y, z1, maxU, minV );
					tessellator.addVertexWithUV( x, y, z1, minU, minV );
					break;

				case EAST:
					tessellator.addVertexWithUV( x1, y, z, maxU, maxV );
					tessellator.addVertexWithUV( x1, y1, z, maxU, minV );
					tessellator.addVertexWithUV( x1, y1, z1, minU, minV );
					tessellator.addVertexWithUV( x1, y, z1, minU, maxV );
					break;

				case NORTH:
					tessellator.addVertexWithUV( x, y, z1, minU, maxV );
					tessellator.addVertexWithUV( x1, y, z1, maxU, maxV );
					tessellator.addVertexWithUV( x1, y1, z1, maxU, minV );
					tessellator.addVertexWithUV( x, y1, z1, minU, minV );
					break;

				case SOUTH:
					tessellator.addVertexWithUV( x, y1, z, maxU, minV );
					tessellator.addVertexWithUV( x1, y1, z, minU, minV );
					tessellator.addVertexWithUV( x1, y, z, minU, maxV );
					tessellator.addVertexWithUV( x, y, z, maxU, maxV );
					break;

				case UP:
					tessellator.addVertexWithUV( x, y1, z1, maxU, minV );
					tessellator.addVertexWithUV( x1, y1, z1, minU, minV );
					tessellator.addVertexWithUV( x1, y1, z, minU, maxV );
					tessellator.addVertexWithUV( x, y1, z, maxU, maxV );
					break;

				case WEST:
					tessellator.addVertexWithUV( x, y, z1, maxU, maxV );
					tessellator.addVertexWithUV( x, y1, z1, maxU, minV );
					tessellator.addVertexWithUV( x, y1, z, minU, minV );
					tessellator.addVertexWithUV( x, y, z, minU, maxV );
					break;

				default:
					// Invalid side.
					break;
			}
		}
	}

	@Override
	public final void renderInventoryBlock( final Block block, final int metadata, final int modelId, final RenderBlocks renderer )
	{
		// Get the tessellator instance
		Tessellator tessellator = Tessellator.instance;

		IIcon texture = this.baseTexture;

		GL11.glTranslatef( -0.5F, -0.5F, -0.5F );

		tessellator.startDrawingQuads();
		tessellator.setNormal( 0.0F, -1.0F, 0.0F );
		renderer.renderFaceYNeg( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal( 0.0F, 1.0F, 0.0F );
		renderer.renderFaceYPos( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal( 0.0F, 0.0F, -1.0F );
		renderer.renderFaceZNeg( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal( 0.0F, 0.0F, 1.0F );
		renderer.renderFaceZPos( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal( -1.0F, 0.0F, 0.0F );
		renderer.renderFaceXNeg( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal( 1.0F, 0.0F, 0.0F );
		renderer.renderFaceXPos( block, 0.0D, 0.0D, 0.0D, texture );
		tessellator.draw();

		GL11.glTranslatef( 0.5F, 0.5F, 0.5F );
	}

	@Override
	public final boolean renderWorldBlock( final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId,
											final RenderBlocks renderer )
	{
		Tessellator tessellator = Tessellator.instance;

		// Texture
		IIcon texture;

		// Should we ignore the actual brightness and go full?
		boolean overrideBrightness = false;

		// Is this the opaque pass?
		if( Renderers.currentRenderPass == Renderers.PASS_OPAQUE )
		{
			// Set the texture to the base
			texture = this.baseTexture;

			// Set the drawing color to full white
			tessellator.setColorRGBA( 255, 255, 255, 255 );

		}
		// This is the alpha pass.
		else
		{
			// Set the texture to the overlay
			texture = this.overlayTexture;

			// Get the provider
			TileProviderBase provider = (TileProviderBase)world.getTileEntity( x, y, z );

			// Get the color of the provider
			AEColor overlayColor = provider.getColor();

			// Get the active state
			boolean isActive = provider.isActive();

			// Is the provider active?
			if( isActive )
			{
				// Adjust brightness
				overrideBrightness = true;
				tessellator.setBrightness( 0xF000F0 );

				// Set the drawing color
				tessellator.setColorOpaque_I( overlayColor.mediumVariant );
			}
			else
			{
				// Inactive, set the drawing color to black
				tessellator.setColorRGBA( 0, 0, 0, 255 );
			}
		}

		// Render the faces
		this.renderFaces( world, x, y, z, texture, !overrideBrightness );

		return true;
	}

	@Override
	public final boolean shouldRender3DInInventory( final int modelId )
	{
		return true;
	}

}
