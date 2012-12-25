package com.imseam.chatpage.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.ExceptionUtil;

/**
 * this class will not be used
 * In the future, it could be used to add an new tag to specify resource bundles for chatpages
 * @author shengjiu
 *
 */

public class LoadBundleTag extends Tag {
	private String var = null;
	private String basename = null;

	public LoadBundleTag(String var, String basename) {
		super(null);

		this.basename = basename;
		this.var = var;
	}

	@Override
	protected void renderTag(ChatpageContext context) {

		if (null == var) {
			throw new NullPointerException("LoadBundle: 'var' must not be null");
		}

		if (context == null) {
			ExceptionUtil.createRuntimeException("No chatpage context?!");
		}

		Locale locale = context.getChannel().getLocale();
		

		if (null == basename) {
			ExceptionUtil.createRuntimeException("LoadBundle: 'basename' must not be null");
		}

		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(basename, locale);
		} catch (MissingResourceException e) {
			try {
				bundle = ResourceBundle.getBundle(basename, locale, this.getClass().getClassLoader());
			} catch (MissingResourceException e1) {
				ExceptionUtil.createRuntimeException("Resource bundle '" + basename + "' could not be found.", e1);
			}
		}

		context.getRequest().setAttribute(var, new BundleMap(bundle));

	}

	private static class BundleMap implements Map<String, String> {
		private ResourceBundle _bundle;
		private List<String> _values;

		public BundleMap(ResourceBundle bundle) {
			_bundle = bundle;
		}

		// Optimized methods

		public String get(Object key) {
			try {
				return (String) _bundle.getObject(key.toString());
			} catch (Exception e) {
				return "???" + key + "???";
			}
		}

		public boolean isEmpty() {
			return !_bundle.getKeys().hasMoreElements();
		}

		public boolean containsKey(Object key) {
			try {
				return _bundle.getObject(key.toString()) != null;
			} catch (MissingResourceException e) {
				return false;
			}
		}

		// Unoptimized methods

		public Collection<String> values() {
			if (_values == null) {
				_values = new ArrayList<String>();
				for (Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements();) {
					String v = _bundle.getString(enumer.nextElement());
					_values.add(v);
				}
			}
			return _values;
		}

		public int size() {
			return values().size();
		}

		public boolean containsValue(Object value) {
			return values().contains(value);
		}

		public Set<Map.Entry<String, String>> entrySet() {
			Set<Entry<String, String>> set = new HashSet<Entry<String, String>>();
			for (Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements();) {
				final String k = enumer.nextElement();
				set.add(new Map.Entry<String, String>() {
					public String getKey() {
						return k;
					}

					public String getValue() {
						return (String) _bundle.getObject(k);
					}

					public String setValue(String value) {
						throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
					}
				});
			}
			return set;
		}

		public Set<String> keySet() {
			Set<String> set = new HashSet<String>();
			for (Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements();) {
				set.add(enumer.nextElement());
			}
			return set;
		}

		// Unsupported methods

		public String remove(Object key) {
			throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
		}

		public void putAll(Map<? extends String, ? extends String> t) {
			throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
		}

		public String put(String key, String value) {
			throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
		}

		public void clear() {
			throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
		}

	}

}
