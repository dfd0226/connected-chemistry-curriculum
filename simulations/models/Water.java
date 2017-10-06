package simulations.models;

import static data.State.molecules;
import static simulations.P5Canvas.*;
import main.Main;
import org.jbox2d.common.Vec2;

import simulations.P5Canvas;

public class Water {
	private P5Canvas p5Canvas;
	public Water(P5Canvas parent)
	{
		p5Canvas = parent;
	}
	public void setForceWater(int indexWater, Molecule mWater) { 
		if (p5Canvas.temp<=0){
			for (int e = 0; e < mWater.getNumElement(); e++) {
				float sumForceX=0;
				float sumForceY=0;
				int indexCharge = mWater.elementCharges.get(e);
				Vec2 locIndex = mWater.getElementLocation(e);
				for (int i = 0; i < molecules.size(); i++) {
					if (i==indexWater)
						continue;
					Molecule m = molecules.get(i);
					float forceX;
					float forceY;
					for (int e2 = 0; e2 < m.getNumElement(); e2++) {
						Vec2 loc = m.getElementLocation(e2);
						if(loc==null || locIndex==null) continue;
						float x = locIndex.x-loc.x;
						float y = locIndex.y-loc.y;
					    float dis = x*x +y*y;
						forceX =  (float) ((x/Math.pow(dis,1.5))*3);
						forceY =  (float) ((y/Math.pow(dis,1.5))*3);
						if (m.getName().equals("Silicon-Dioxide")){
							forceX *=0.1;
							forceY *=0.1;
						}
						else if (m.getName().equals("Glycerol")){
							forceX *=0.08;
							forceY *=0.08;
						}
						else if (m.getName().equals("Acetic-Acid")){
							forceX *=0.3;
							forceY *=0.3;
						}
						else if (m.getName().equals("Pentane")){
							forceX *=0.08;
							forceY *=0.08;
						}
						else if (m.getName().equals("Oxygen")){
							forceX *=0.0;
							forceY *=0.0;
						}
						else if (m.getName().equals("Hydrogen-Peroxide")){
							forceX *=0.02;
							forceY *=0.02;
						}
						
						int charge = m.elementCharges.get(e2);
						int mul = charge*indexCharge;
						if (mul<0){
							sumForceX += mul*forceX;
							sumForceY += mul*forceY;
						}
						else if (charge*indexCharge>0){
							sumForceX += mul*forceX*mWater.chargeRate;
							sumForceY += mul*forceY*mWater.chargeRate;
						}
					}
				}
				mWater.addForce(new Vec2(sumForceX,sumForceY), e);
			}
		
		}
		else if (p5Canvas.getUnit()==1 && p5Canvas.getSim()!=3){
			for (int i = 0; i < molecules.size(); i++) {
				if (i==indexWater)
					continue;
				Molecule m = molecules.get(i);
				Vec2 loc = m.getPosition();
				Vec2 locIndex = mWater.getPosition();
				if(loc==null || locIndex==null) continue;
				float x = locIndex.x-loc.x;
				float y = locIndex.y-loc.y;
			   float dis = x*x +y*y;
				Vec2 normV = normalizeForce(new Vec2(x,y));
				float forceX;
				float forceY;
				float attractForce=1;
				if (mWater.polarity==m.polarity){
					float fTemp = mWater.freezingTem;
					float bTemp = mWater.boilingTem;
					float gravityX,gravityY;
					if (p5Canvas.temp>=bTemp){ //Gas case
						gravityX = 0;
						gravityY = 0;
					}
					else if (p5Canvas.temp<=fTemp){ //Solid case
						gravityY = (bTemp-p5Canvas.temp)/(bTemp-fTemp);
						gravityX = gravityY*2f;
					}	
					else{ //Liquid case
						gravityY = (bTemp-p5Canvas.temp)/(bTemp-fTemp);
						gravityX = gravityY*0.6f;
					}	
					forceX =  (-normV.x/dis)*m.getBodyMass()*mWater.getBodyMass()*gravityX*attractForce;
					forceY =  (-normV.y/dis)*m.getBodyMass()*mWater.getBodyMass()*gravityY*attractForce;
				}	
				else{
					float num = m.getNumElement();
					forceX =  (normV.x/dis)*m.getBodyMass()*mWater.getBodyMass()*300*num;
					forceY =  (normV.y/dis)*m.getBodyMass()*mWater.getBodyMass()*300*num;
				}
				mWater.addForce(new Vec2(forceX,forceY));
			}
		}
		else {
			for (int e = 0; e < mWater.getNumElement(); e++) {
				float sumForceX=0;
				float sumForceY=0;
				int indexCharge = mWater.elementCharges.get(e);
				Vec2 locIndex = mWater.getElementLocation(e);
				for (int i = 0; i < molecules.size(); i++) {
					Molecule m = molecules.get(i);
					if (m.getName().equals("Water"))
						continue;
					float forceX;
					float forceY;
					for (int e2 = 0; e2 < m.getNumElement(); e2++) {
						Vec2 loc = m.getElementLocation(e2);
						float x = locIndex.x-loc.x;
						float y = locIndex.y-loc.y;
					    float dis = x*x +y*y;
						forceX =  (float) (x/Math.pow(dis,1.5));
						forceY =  (float) (y/Math.pow(dis,1.5));
						int charge = m.elementCharges.get(e2);
						if (m.getName().equals("Chlorine-Ion") ){
							forceX *=2;
							forceY *=2;
						}
						else if (m.getName().equals("Sodium-Ion")){
							forceX *=3f;
							forceY *=3f;
						}
						else if (m.getName().equals("Calcium-Ion")){
							forceX *=3f;
							forceY *=3f;
						}
						else if (m.getName().equals("Glycerol")){
							forceX *=0.05;
							forceY *=0.05;
						}
						else if (m.getName().equals("Acetic-Acid")){
							forceX *=0.4;
							forceY *=0.4;
						}
						else if (m.getName().equals("Pentane")){
							forceX *=0.08;
							forceY *=0.08;
						}
						else if (m.getName().equals("Bicarbonate")){
							forceX *=1;
							forceY *=1;
						}
						else if (m.getName().equals("Potassium-Ion")){
							forceX *=3f;
							forceY *=3f;
						}
						float mul = charge*indexCharge;
						if (mWater.elementNames.get(e).equals("Oxygen"))
							mul *=0.8;
						if (mul<0){
							sumForceX += mul*forceX;
							sumForceY += mul*forceY;
						}
						else if (mul>0){
							sumForceX += mul*forceX*mWater.chargeRate;
							sumForceY += mul*forceY*mWater.chargeRate;
						}
					}
				}
				mWater.addForce(new Vec2(sumForceX,sumForceY), e);
			}
		}
	}
	public Vec2 normalizeForce(Vec2 v){
		float dis = (float) Math.sqrt(v.x*v.x + v.y*v.y);
		return new Vec2(v.x/dis,v.y/dis);
	}
}
