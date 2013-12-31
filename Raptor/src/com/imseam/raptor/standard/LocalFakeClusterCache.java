package com.imseam.raptor.standard;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.imseam.cluster.ClusterLockException;
import com.imseam.cluster.IClusterList;
import com.imseam.cluster.IClusterMap;
import com.imseam.cluster.IClusterSet;
import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.IFutureResult;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IRaptorClustercache;

public class LocalFakeClusterCache implements IRaptorClustercache {

	private ConcurrentHashMap<String, Object> clusterObjectMap = new ConcurrentHashMap<String, Object>();
	private ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();

	@Override
	public <T> void put(String key, T obj) {
		@SuppressWarnings("unchecked")
		T oldObj = (T) clusterObjectMap.put(key, obj);
	}

	@Override
	public <T> T putIfAbsent(String key, T obj) {

		T oldObj = (T) clusterObjectMap.putIfAbsent(key, obj);
		return oldObj;
	}

	@Override
	public void remove(String... keys) {

		clusterObjectMap.remove(keys);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) clusterObjectMap.get(key);
	}

	@Override
	public void init(IChatletApplication application) {
	}

	@Override
	public void lock(String... keys) throws ClusterLockException {
		for(String key : keys){
			ReentrantLock lock = null;
			ReentrantLock existingLock = this.lockMap.get(key);
			if(existingLock != null){
				lock = new ReentrantLock();
				existingLock = this.lockMap.putIfAbsent(key, lock);
				if(existingLock != null){
					lock = existingLock;
				}
			}else{
				lock = existingLock;
			}
			lock.lock();
		}
		
		/**
		 * option 1 only one global lock
		 * option 2 use putifabsent
		 * like the redis
		 * 
		 * option 3 lock for each key
		 * 	putifabsent, and then lock
		**/

	}

	@Override
	public void unlock(String... keys) {
		for(String key : keys){
			ReentrantLock existingLock = this.lockMap.get(key);
			existingLock.unlock();
		}
	}

	@Override
	public IClusterTransaction startTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> get(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<IFutureResult<T>> getInFuture(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> IFutureResult<T> getInFuture(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> IClusterSet<T> getSet(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> IClusterMap<T> getMap(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> IClusterList<T> getList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseToPool() {
		// TODO Auto-generated method stub

	}

}
