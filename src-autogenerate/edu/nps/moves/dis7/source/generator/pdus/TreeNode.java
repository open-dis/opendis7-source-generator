/**
 * Copyright (c) 2008-2022, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

import java.util.*;

/**
 *
 * @author DMcG
 */
public class TreeNode // TODO consider refactor renaming as PythonTreeNode
{
    GeneratedClass aClass = null;
    List<TreeNode> children = new ArrayList<>();

    /** Constructor
     * @param aClass GeneratedClass of interest */
    public TreeNode(GeneratedClass aClass)
    {
        this.aClass = aClass;
    }
    
    /**
     * Find appropriate TreeNode object using class name
     * @param name of interest
     * @return appropriate TreeNode object
     */
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
    
    /**
     * Add class to tree
     * @param aClass class of interest
     */
    public void addClass(GeneratedClass aClass)
    {
        children.add(new TreeNode(aClass));
    }
    
    /**
     * Get list of classes
     * @param aList TreeNode list
     */
    public void getList(List<TreeNode> aList)
    {
        aList.addAll(children);
        
        for (TreeNode aNode : children) {
            aNode.getList(aList);
        }
        
    }

}
