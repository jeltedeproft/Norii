package com.jelte.norii.shaders;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class PixelScalerEffect extends ShaderVfxEffect implements ChainVfxEffect {
    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TIME = "u_time";
    private static final String U_SPEED = "u_speed";
    private static final String U_AMOUNT = "u_amount";
    private static final String U_SHARPNESS = "u_sharpness";
    
    private float speed = .6f; //0 - 1 speed
    private float amount = .2f; //0 -1 glitch amount
    private float time = 0f;
    private float sharpness = 0.2f;
    
    public PixelScalerEffect() {
        super(VfxGLUtils.compileShader(Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"), Gdx.files.internal("shaders/pixelScaler.frag")));
        rebind();
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        setTime(this.time + delta);
    }
    
    public float getTime() {
        return time;
    }
    
    public void setTime(float time) {
        this.time = time;
        setUniform(U_TIME, time);
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
        setUniform(U_SPEED, speed);
    }
    
    public float getAmount() {
        return amount;
    }
    
    public void setAmount(float amount) {
        this.amount = amount;
        setUniform(U_AMOUNT, amount);
    }
    
    public float getSharpness() {
		return sharpness;
	}

	public void setSharpness(float sharpness) {
		this.sharpness = sharpness;
		setUniform(U_SHARPNESS, sharpness);
	}

	@Override
    public void rebind() {
        super.rebind();
        program.bind();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_TIME, time);
        program.setUniformf(U_SPEED, speed);
        program.setUniformf(U_AMOUNT, amount);
        program.setUniformf(U_SHARPNESS, sharpness);
    }
    
    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        render(context, buffers.getSrcBuffer(), buffers.getDstBuffer());
    }
    
    public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
        // Bind src buffer's texture as a primary one.
        src.getTexture().bind(TEXTURE_HANDLE0);
        // Apply shader effect and render result to dst buffer.
        renderShader(context, dst);
    }
}