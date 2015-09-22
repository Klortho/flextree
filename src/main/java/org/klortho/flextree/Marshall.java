package org.klortho.flextree;



import org.klortho.flextree.LayoutEngine.WrappedTree;

public class Marshall{




	public Object convert(Tree root) {
		if(root == null) return null;
		WrappedTree[] children = new WrappedTree[root.children.size()];
		for(int i = 0 ; i < children.length ; i++){
			children[i] = (WrappedTree) convert(root.children.get(i));
		}
		return new WrappedTree(root.width,root.height,root.y, children);
	}


	public void convertBack(Object converted, Tree root) {
		WrappedTree conv = (WrappedTree)converted;
		root.x = conv.x;
		for(int i = 0 ; i < conv.c.length ; i++){
			convertBack(conv.c[i], root.children.get(i));
		}
		
	}

	public void runOnConverted(Object root) {
		LayoutEngine.layout((WrappedTree) root);
	}
}
