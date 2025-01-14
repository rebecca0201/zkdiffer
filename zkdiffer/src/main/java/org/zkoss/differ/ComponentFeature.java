/* ComponentFeature.java

	Purpose:
		
	Description:
		
	History:
		10:08 AM 2023/5/4, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.differ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Represents a feature of a ZK component for diffing.
 * @author jumperchen
 */
public class ComponentFeature implements Cloneable {
	private Feature _feature;
	private String _widgetName;

	private Component _owner;
	private List<ComponentFeature> _children = new ArrayList<>();

	private Map<String, Object> _attributes;
	private Map<String, Object> _dynamicProperties;
	/*package*/ boolean outerDone;
	/*package*/ boolean innerDone;

	private ComponentFeature(Feature feature, String widgetName, Component owner) {
		_feature = feature;
		_widgetName = widgetName;
		_owner = owner;
		_attributes = new HashMap<>(owner.getAttributes());
		_dynamicProperties = owner instanceof DynamicPropertied ? ((DynamicPropertied) owner).getDynamicProperties() : new HashMap<>(0);
	}

	/**
	 * Returns the uuid of the ZK component. (Never null)
	 */
	public String getUuid() {
		return _owner.getUuid();
	}

	/**
	 * Returns the widget name of the ZK component (Never null)
	 * @return
	 */
	public String getWidgetName() {
		return _widgetName;
	}

	/**
	 * Returns the id of the ZK component, if any.
	 */
	@Nullable
	public String getId() {
		return (String) _feature._properties.get("id");
	}

	/**
	 * Returns the text value for input element or the label of the ZK component, if any.
	 */
	@Nullable
	public Object getTextValue() {
		if ("label".equals(_widgetName)) {
			return _feature._properties.get("value");
		}
		Object label = _feature._properties.get("label");
		if (label != null) return label;
		return _feature._properties.get("_value"); // for InputElement
	}

	/**
	 * Returns all component dynamic properties
	 */
	public Map<String, Object> getDynamicProperties() {
		return _dynamicProperties;
	}

	/**
	 * Sets a new dynamic property or update a dynamic property.
	 * @param name the name of the dynamic property
	 * @param value
	 */
	public void setDynamicProperty(String name, Object value) {
		_dynamicProperties.put(name, value);
	}

	/**
	 * Removes the dynamic property from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeDynamicProperty(String name) {
		return _dynamicProperties.remove(name);
	}

	/**
	 * Returns all component attributes
	 */
	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	/**
	 * Sets a new attribute or update an attribute.
	 * @param name the name of the attribute
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		_attributes.put(name, value);
	}

	/**
	 * Removes the attribute from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeAttribute(String name) {
		return _attributes.remove(name);
	}

	/**
	 * Returns all properties generated by {@link ComponentCtrl#renderPropertiesOnly(ContentRenderer)}
	 */
	public Map<String, Object> getProperties() {
		return _feature._properties;
	}

	/**
	 * Sets a new property or update a property.
	 * @param name the name of the property
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		_feature._properties.put(name, value);
	}

	/**
	 * Removes the property from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeProperty(String name) {
		return _feature._properties.remove(name);
	}

	/**
	 * Returns all widget overrides
	 */
	public Map<String, String> getWidgetOverrides() {
		return _feature._overrides;
	}

	/**
	 * Sets a new widget overide or update a widget override.
	 * @param name the name of the override
	 * @param value
	 */
	public void setWidgetOverride(String name, String value) {
		if (_feature._overrides == null) {
			_feature._overrides = new HashMap<>(4);
		}
		_feature._overrides.put(name, value);
	}

	/**
	 * Removes the widget override from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeWidgetOverride(String name) {
		if (_feature._overrides == null) return null;
		return _feature._overrides.remove(name);
	}

	/**
	 * Returns all client attributes
	 */
	public Map<String, String> getClientAttributes() {
		return _feature._clientAttrs;
	}

	/**
	 * Sets a new client attribte or update a client attribute
	 * @param name the name of the client attribute
	 * @param value
	 */
	public void setClientAttribute(String name, String value) {
		if (_feature._clientAttrs == null) {
			_feature._clientAttrs = new HashMap<>(4);
		}
		_feature._clientAttrs.put(name, value);
	}

	/**
	 * Removes the client attribute from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeClientAttribute(String name) {
		if (_feature._clientAttrs == null) return null;
		return _feature._clientAttrs.remove(name);
	}

	/**
	 * Returns all widget attributes
	 */
	public Map<String, String> getWidgetAttributes() {
		return _feature._widgetAttrs;
	}

	/**
	 * Sets a new widget attribte or update a widget attribute
	 * @param name the name of the widget attribute
	 * @param value
	 */
	public void setWidgetAttribute(String name, String value) {
		if (_feature._widgetAttrs == null) {
			_feature._widgetAttrs = new HashMap<>(4);
		}
		_feature._widgetAttrs.put(name, value);
	}

	/**
	 * Removes the widget attribute from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeWidgetAttribute(String name) {
		if (_feature._widgetAttrs == null) {
			return null;
		}
		return _feature._widgetAttrs.remove(name);
	}

	/**
	 * Returns all widget listeners
	 */
	public Map<String, String> getWidgetListeners() {
		return _feature._widgetListeners;
	}

	/**
	 * Sets a new widget listener or update a widget listener
	 * @param name the name of the widget listener
	 * @param value
	 */
	public void setWidgetListener(String name, String value) {
		if (_feature._widgetListeners == null) {
			_feature._widgetListeners = new HashMap<>(4);
		}
		_feature._widgetListeners.put(name, value);
	}

	/**
	 * Removes the widget listener from the given name.
	 * @param name
	 * @return the origin value if any.
	 */
	public Object removeWidgetListener(String name) {
		if (_feature._widgetListeners == null) {
			return null;
		}
		return _feature._widgetListeners.remove(name);
	}

	/**
	 * Appends a child of component feature to the end of the children of this instance.
	 * @param newChild a component feature as a child of this instance.
	 */
	public void appendChild(ComponentFeature newChild) {
		_children.add(newChild);
	}

	/**
	 * Adds a child of component feature at the given index to the children of this instance.
	 * @param index the child index to be inserted.
	 * @param newChild a component feature as a child of this instance.
	 */
	public void addChild(int index, ComponentFeature newChild) {
		_children.add(index, newChild);
	}

	/**
	 * Removes the child from the given index.
	 * @param index the child index
	 * @return the origin child.
	 */
	public ComponentFeature removeChild(int index) {
		return _children.remove(index);
	}

	/**
	 * Replaces a child from the given index and new child.
	 */
	public void replaceChild(int index, ComponentFeature newChild) {
		_children.set(index, newChild);
	}

	/**
	 * Returns all children of this instance.
	 */
	public List<ComponentFeature> getChildren() {
		return _children;
	}

	/**
	 * Matches the given component feature with this instance.
	 * @return true if all features are matched.
	 */
	public boolean match(ComponentFeature other) {
		if (this == other) return true;
		return _feature.match(other._feature);
	}

	/**
	 * Returns the descriptors of this component. (Never null)
	 */
	public List<String> getDescriptors() {
		List<String> output = new ArrayList<>();
		output.add(_widgetName);
		String sclass = (String) getProperties().get("sclass");
		if (sclass != null) {
			output.add(_widgetName + "." + (sclass.replace(" ", ".")));
		}
		String id = getId();
		if (id != null) {
			output.add(_widgetName + "#" + id);
		}
		return output;
	}

	/**
	 * Converts the feature to ZK component. (including all children)
	 */
	public Component toComponent() {
		return (Component) _owner.clone();
	}

	/**
	 * Builds a component feature tree from the given ZK component tree.
	 * <p>Note: the source component will be cloned as a snapshot inside this method.
	 * @param source
	 * @param options
	 */
	public static ComponentFeature build(Component source, DiffOptions options) {
		if (source == null) {
			throw new RuntimeException("Component cannot be null");
		}
		if (!(source instanceof ComponentCtrl)) {
			throw new RuntimeException("Component should implement ComponentCtrl interface [" + source + "]");
		}
		Component clone = (Component) source.clone();

		// unlink clone's shadow roots
		ComponentCtrl ctrl = ((ComponentCtrl) clone);
		List<ShadowElement> shadowRoots = ctrl.getShadowRoots();

		if (shadowRoots != null && !shadowRoots.isEmpty()) {
			for (ShadowElement root : new ArrayList<>(shadowRoots)) {
				ctrl.removeShadowRoot(root);
			}
		}

		// support for ForEach that should exclude all previousInsertions and nextInsertions from the host.
		DiffRange diffRange = options.getSourceRange();
		if (diffRange != null) {
			Iterator<Component> iter = clone.getChildren().iterator();
			for (int start = 0; iter.hasNext(); start++) {
				iter.next();
				if (start < diffRange.getStart()) {
					iter.remove();
				} else if (start >= diffRange.getEnd()) {
					iter.remove();
				}
			}
		}
		return buildNested(clone);
	}

	/**
	 * Internal use only.
	 * <p>Note: unlike {@link #build(Component, DiffOptions)}, this source component
	 * won't be cloned inside this method, i.e. the ComponentFeature hold a live component.
	 * @hidden
	 */
	public static ComponentFeature buildNested(Component source) {
		ComponentFeature root = new ComponentFeature(new Feature(), source.getDefinition().getName(), source);
		List<Component> shadowChildren = new ArrayList<>(source.getChildren());
		try {
			((ComponentCtrl) source).renderPropertiesOnly(root._feature);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		shadowChildren.forEach((component -> root.appendChild(ComponentFeature.buildNested(component))));
		return root;
	}

	/**
	 * Clones a new component feature.
	 */
	public ComponentFeature clone() {
		try {
			ComponentFeature clone = (ComponentFeature) super.clone();
			clone._widgetName = _widgetName;
			clone._feature = _feature.clone();
			clone._attributes = new HashMap<>(_attributes);
			clone._dynamicProperties = new HashMap<>(_dynamicProperties);
			clone._children = _children.stream().map(ComponentFeature::clone).collect(
					Collectors.toList());

//			1. Avoid to clone ZK component recursively
//			2. ZK component will be cloned when patching
//			clone._owner = (Component) _owner.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	private static class Feature implements ContentRenderer, Cloneable {
		private Map<String, Object> _properties;
		private Map<String, String> _overrides;
		private Map<String, String> _clientAttrs;
		private Map<String, String> _widgetAttrs;
		private Map<String, String> _widgetListeners;
		private Feature() {
			_properties = new HashMap<>();
		}
		public int size() {
			return _properties.size();
		}
		public Set<String> keySet() {
			return _properties.keySet();
		}
		public boolean containsKey(String key) {
			return _properties.containsKey(key);
		}

		public void render(String name, String value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, Date value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, Object value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, int value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, short value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, long value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, byte value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, boolean value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, double value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, float value) throws IOException {
			_properties.put(name, value);
		}

		public void render(String name, char value) throws IOException {
			_properties.put(name, value);
		}

		public void renderDirectly(String name, Object value) {
			_properties.put(name, value);
		}

		public void renderWidgetListeners(Map<String, String> listeners) {
			_widgetListeners = listeners;
		}

		public void renderWidgetOverrides(Map<String, String> overrides) {
			_overrides = overrides;
		}

		public void renderWidgetAttributes(Map<String, String> attrs) {
			_widgetAttrs = attrs;
		}

		public void renderClientAttributes(Map<String, String> attrs) {
			_clientAttrs = attrs;
		}

		public boolean match(Feature other) {
			if (this == other) return true;
			return equalsMap(_properties, other._properties) && equalsMap(_overrides, other._overrides)
					&& equalsMap(_clientAttrs, other._clientAttrs) && equalsMap(_widgetAttrs, other._widgetAttrs)
					&& equalsMap(_widgetListeners, other._widgetListeners);
		}

		private static boolean equalsMap(Map m1, Map m2) {
			if (m1 != null && m2 != null) {
				if (m1.size() != m2.size()) {
					return false;
				}
				return m1.keySet().stream()
						.allMatch(k -> Objects.equals(m1.get(k), m2.get(k)));
			} else
				return m1 == null && m2 == null;
		}

		public Feature clone() {
			try {
				Feature clone = (Feature) super.clone();
				clone._properties = new HashMap<>(_properties);
				clone._overrides = _overrides != null ? new HashMap<>(_overrides) : _overrides;
				clone._clientAttrs = _clientAttrs != null ? new HashMap<>(_clientAttrs) : _clientAttrs;
				clone._widgetListeners = _widgetListeners != null ? new HashMap<>(_widgetListeners) : _widgetListeners;
				clone._widgetAttrs = _widgetAttrs != null ? new HashMap<>(_widgetAttrs) : _widgetAttrs;
				return clone;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (!_properties.isEmpty()) {
				_properties.forEach((key, value) -> {
					sb.append(key).append('=').append(value).append(';');
				});
			}
			return sb.toString();
		}
	}
}
