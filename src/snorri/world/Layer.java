package snorri.world;

public interface Layer extends Editable {

	@Override
	public Layer getTransposed();
	@Override
	public Layer getXReflected();
	
	public Layer getResized(int newWidth, int newHeight, int i);

	public int getHeight();
	public int getWidth();
	
	public boolean canShootOver(Vector g);	
}
