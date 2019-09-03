/**
 * Copyright (c) 2008-2019, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 */
package edu.nps.moves.dis7.source.generator.pdus;

import java.util.*;

/**
 *
 * @author DMcG
 */
public class TreeNode 
{
    GeneratedClass aClass = null;
    List<TreeNode> children = new ArrayList<TreeNode>();

    public TreeNode(GeneratedClass aClass)
    {
        this.aClass = aClass;
    }
    
    public TreeNode findClass(String name)
    {
        if(aClass != null && aClass.getName().equalsIgnoreCase(name))
            return this;
        
        for(int idx = 0; idx < children.size(); idx++)
        {
            TreeNode aNode = children.get(idx);
            if(aNode.findClass(name) != null)
                return aNode;
        }
        
        return null; 
    }
    
    public void addClass(GeneratedClass aClass)
    {
        children.add(new TreeNode(aClass));
    }
    
    public void getList(List aList)
    {
        aList.addAll(children);
        
        Iterator it = children.iterator();
        while(it.hasNext())
        {
            TreeNode aNode = (TreeNode)it.next();
            aNode.getList(aList);
        }
        
    }

}
