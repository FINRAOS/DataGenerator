/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.input;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.AppConstants;

/**
 * Represent a specificiation for a group. Name, number per parent, elements that are required to be unique, and parent
 * group name.
 *
 * @author ChamberA
 *
 */
public class GroupSpec {

    private static Logger LOG = Logger.getLogger(GroupSpec.class);

    private final String name;
    private int numPerParent = 1;
    private List<String> uniqueElements = new ArrayList<String>();
    private String parentGroupType = AppConstants.ROOT_GROUP;
    private Map<String, VariableSpec> memberVariableSpecs = new HashMap<String, VariableSpec>();
    private UniqueElementsComboGenerator comboGen;

    public GroupSpec(String name) {
        this.name = name;
    }

    // getters
    public String getName() {
        return new String(name);
    }

    public int getNumPerParent() {
        return numPerParent;
    }

    public String getParentGroupType() {
        return parentGroupType;
    }

    public List<String> getUniqueElements() {
        return uniqueElements;
    }

    public void setNumPerParent(String n) {
        this.numPerParent = Integer.parseInt(n);
    }

    public void setNumPerParent(int n) {
        this.numPerParent = n;
    }

    public void addUniqueElement(String elem) {
        uniqueElements.add(elem);
    }

    public boolean requiresUniqueElems() {
        return !uniqueElements.isEmpty();
    }

    public void setParentGroupName(String pName) {
        this.parentGroupType = pName;
    }

    /**
     * Links this variable spec as a member of this group
     *
     * @param varSpec
     */
    public void addVariableMember(VariableSpec varSpec) {
        memberVariableSpecs.put(varSpec.getName(), varSpec);
    }

    public Collection<VariableSpec> getAllMemberVariableSpecs() {
        return memberVariableSpecs.values();
    }

    /**
     * If this group has uniqueness requirements, then this method will return the ith unique combination as
     * vartype:value pairs, or throw an exception if all combinations have been exhausted.
     *
     * @return
     */
    public synchronized Map<String, String> getUniqueCombo(int i) {
        Preconditions.checkArgument(this.requiresUniqueElems(), name+" does not have any unique element requirements");
        if (comboGen==null) {
            comboGen = new UniqueElementsComboGenerator();
        }
        return comboGen.getUniqueCombo(i);
    }

    private class UniqueElementsComboGenerator {

        // to store the current combo as integers
        private ArrayList<Integer> curCombo = new ArrayList<Integer>(uniqueElements.size());
        // the options for each element
        private ArrayList<List<String>> options = new ArrayList<List<String>>(uniqueElements.size());
        private int possibleCombos;
        private ArrayList<Map<String, String>> memoizedCombos = new ArrayList<Map<String, String>>();

        private UniqueElementsComboGenerator() {
            // initialize the list of options for each unique element
            for(String uniquElem : uniqueElements){
                options.add(memberVariableSpecs.get(uniquElem).getPropertySpec(AppConstants.VALUE).getPositiveValues());
            }
            // count the possible combos
            possibleCombos = 1;
            for(List<String> optionList : options){
                possibleCombos *= optionList.size();
            }
        }

        /**
         * Interprets the ints from combo to make a Varname:Value map
         */
        private Map<String, String> getCurrentComboAsMap() {
            Map<String, String> comboAsMap = new HashMap<String, String>();
            for(int i = 0; i<uniqueElements.size(); ++i){
                String elementName = uniqueElements.get(i);
                String value = options.get(i).get(curCombo.get(i));
                comboAsMap.put(elementName, value);
            }
            return comboAsMap;
        }

        private void generateNextCombo() {
            if (curCombo.isEmpty()) { // the first combo is option 0 for each element
                for(int i = 0; i<uniqueElements.size(); ++i){
                    curCombo.add(0);
                }
            } else {
                for(int i = 0; i<curCombo.size(); ++i){
                    if (curCombo.get(i)==options.get(i).size()-1) {
                        // if we have exhausted the options for this element then roll the number over to 0
                        curCombo.set(i, 0);
                        // if this is the last element, then there are no more combinations!
                        if (i==curCombo.size()-1) {
                            throw new NoSuchElementException("Combinations exhausted for group: "+name);
                        }
                    } else {
                        // we make a new combo by incrementing this count
                        curCombo.set(i, curCombo.get(i)+1);
                        break;
                    }
                }
            }
            memoizedCombos.add(getCurrentComboAsMap());
        }

        private Map<String, String> getUniqueCombo(int i) {
            Preconditions.checkArgument(i>=0, "Combo number must be >= 0");
            Preconditions.checkArgument(i<possibleCombos, "Only "+possibleCombos+" unique combos are possible for group: "+name+" elems: "+uniqueElements);
            while (memoizedCombos.size()<i+1){
                generateNextCombo();
            }
            return memoizedCombos.get(i);
        }
    }

}
