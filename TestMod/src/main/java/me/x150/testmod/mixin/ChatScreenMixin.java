package me.x150.testmod.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    void postInit(CallbackInfo ci) {
        //        ButtonWidget lidar = ButtonWidget.builder(Text.of("lidar"), button -> {
        //            Handler.lidar();
        //        }).dimensions(5, 5, 100, 20).build();
        //        this.addDrawableChild(lidar);
    }
}
