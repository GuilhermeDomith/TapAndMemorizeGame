package br.com.tsi.ifsemg.bd;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Database {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();;

    public static <T> boolean insert(String fieldName, T objectT){
        DatabaseReference objectsReference = database.getReference(fieldName);
        String id = UUID.randomUUID().toString();
        objectsReference.child(id).setValue(objectT);
        return true;
    }

    public static <T> void getValue(String fieldName, final Class<T> classValue,  final GetObjectListener<T> listener){
        DatabaseReference objectReference = database.getReference(fieldName);
        objectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.getObject(dataSnapshot.getValue(classValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.getObject(null);
            }
        });

    }

    public static <T> void all(String fieldName, final Class<T> classValue, final ListObjectsListener<T> listener){
        DatabaseReference objectReference = database.getReference(fieldName);
        Query query = objectReference.orderByValue();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<T> list = new ArrayList<T>();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    list.add(data.getValue(classValue));
                }
                listener.listObjects(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.listObjects(null);
            }
        });
    }

    @FunctionalInterface
    public static interface ListObjectsListener<T>{
        public void listObjects(List<T> objects);
    }

    @FunctionalInterface
    public static interface GetObjectListener<T>{
        public void getObject(T object);
    }
}

