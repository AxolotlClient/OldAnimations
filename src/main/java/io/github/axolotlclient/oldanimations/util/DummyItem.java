package io.github.axolotlclient.oldanimations.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//todo: this might not be the most ideal way of doing this
public class DummyItem extends Item {

	public static ItemStack getStack() {
		return new ItemStack(new DummyItem());
	}

	public static BakedModel getModelFromID(String model) {
		BakedModelManager manager = MinecraftClient.getInstance().getBlockRenderManager().getModels().getBakedModelManager();
		return manager.getByIdentifier(new ModelIdentifier(model, "inventory"));
	}
}
