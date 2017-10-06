package Util;

import org.jbox2d.common.Vec2;

public class MathFunction {
	
	public MathFunction()
	
	{
		
	}
	
	//Normalize the input force
	public static Vec2 normalizeForce(Vec2 v) {
		float dis = (float)Math.sqrt(v.x * v.x + v.y * v.y);
		return new Vec2(v.x / dis, v.y / dis);
	}
	
	public static float computeDistance(Vec2 v1, Vec2 v2) {
		float dis = 0;
		dis = (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y);
		dis = (float) Math.sqrt(dis);

		return dis;
	}
	
	public static float norm(Vec2 v)
	{
		return (float) Math.sqrt(v.x*v.x + v.y * v.y );
	}

}
