package ch.parolini.truxmanager.delivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ch.parolini.truxmanager.delivery.model.Bon;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Bon} and makes a call to the
 * specified {@link }.
 * TODO: Replace the implementation with code for your data type.
 */
public class ListBonAdapter extends RecyclerView.Adapter<ListBonAdapter.ViewHolder> {

    public final List<Bon> _bons;
    public Context context;
    public final List<Bon> mValues;

    public ListBonAdapter(Context context, List<Bon> bons) {
        _bons = bons;
        this.context = context;

        mValues = bons;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.fragment_item_bon, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.Bon = _bons.get( position );
        holder._textViewnumero.setText( _bons.get( position ).getNumero()) ;
        holder._textViewnom.setText( _bons.get( position ).getNom() );
        holder._textViewplaque.setText( _bons.get( position ).getPlaque() );
        holder._viewBon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );
    }

    @Override
    public int getItemCount() {
        return _bons.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View _viewBon;
        public final LinearLayout _linearlayoutBon;
        public final TextView _textViewnom;
        public final TextView _textViewplaque;
        public final TextView _textViewnumero;

        public Bon Bon;

        public ViewHolder(View view) {
            super( view );
            _viewBon = view;
            _linearlayoutBon = view.findViewById( R.id.lin_bon );
            _textViewnom = view.findViewById( R.id.nom);
            _textViewnumero = view.findViewById( R.id.numero );
            _textViewplaque = view.findViewById( R.id.plaque );

        }

        @Override
        public String toString() {
            return super.toString() + " '" + _textViewnom.getText() + "'"+ " '" + _textViewnom.getText() + "'";
        }
    }
}

