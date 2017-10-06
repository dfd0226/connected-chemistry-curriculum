package Util;

public class Integrator {
	
	private float damping ;
	private float attraction ;

	private float value;
	private float vel;
	private float accel;
	private float force =0;
	private float mass = 1;
	
	private boolean targeting = false;  //If integrator is targeting the goal
	
	private boolean interpolating = true;  //If integrator is enabled
	private float target;
	
	public Integrator(float value)
	{
		this.value = value;
		damping = 0.5f;
		attraction = 0.2f;
	}
	
	public Integrator(float value, int d, int a)
	{
		this.value = value;
		damping = d;
		attraction = a;
	}
	
	public void set (float v)
	{
		value = v;
	}
	
	public void setDamping(float d)
	{
		damping = d;
	}
	public void setAttraction(float a)
	{
		attraction = a;
	}
	
	public void update(){
		if(interpolating)
		{
		
			if(targeting)
			{
				force +=attraction * (target-value);
			
			
				accel = force/mass;
				vel = (vel + accel)*damping;
				value += vel;
				
				force = 0;

			}
		}
		else
		{
			value = target;
		}
		
		if(Math.abs((target-value)/value)<0.01)
			setTargeting(false);
		
	}
	
	public void target(float t)
	{
		targeting = true;
		target = t;
	}
	
//	public void disable(){
//		targeting = false;
//	}
	
	public void setTargeting(boolean b)
	{
		targeting = b;
	}
	
	public boolean isTargeting()
	{
		return targeting;
	}


	public float getValue()
	{
		return value;
	}
	public float getVelocity()
	{
		return vel;
	}
	public float getTarget()
	{
		return target;
	}
	
	public void setInterpolating(boolean b)
	{
		interpolating = b;
	}
	
	public void reset()
	{
		setTargeting(false);
		value = target = 0;
	}
}
