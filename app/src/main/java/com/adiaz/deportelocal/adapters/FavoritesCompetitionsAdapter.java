package com.adiaz.deportelocal.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adiaz.deportelocal.R;
import com.adiaz.deportelocal.entities.Competition;
import com.adiaz.deportelocal.utilities.DeporteLocalUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Created by toni on 31/03/2017. */

public class FavoritesCompetitionsAdapter extends RecyclerView.Adapter<FavoritesCompetitionsAdapter.ViewHolder> {

	private Context context;
	private ListItemClickListener listItemClickListener;

	private List<Competition> competitionsFavorites;

	public FavoritesCompetitionsAdapter(Context context, ListItemClickListener listItemClickListener) {
		this.context = context;
		this.listItemClickListener = listItemClickListener;
	}

	public void setCompetitionsFavorites(List<Competition> competitionsFavorites) {
		this.competitionsFavorites = competitionsFavorites;
		notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.listitem_favorites, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.tvTitle.setText(competitionsFavorites.get(position).name());
		holder.tvSubTitle01.setText(DeporteLocalUtils.getStringResourceByName(context, competitionsFavorites.get(position).sportName()));
		holder.tvSubTitle02.setText(DeporteLocalUtils.getStringResourceByName(context,competitionsFavorites.get(position).categoryName()));
		holder.cvCompetitions.setTag(competitionsFavorites.get(position).serverId());
	}

	@Override
	public int getItemCount() {
		return competitionsFavorites.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.tv_fav_title) TextView tvTitle;
		@BindView(R.id.tv_fav_subtitle01) TextView tvSubTitle01;
		@BindView(R.id.tv_fav_subtitle02) TextView tvSubTitle02;
		@BindView(R.id.cv_fav_competition) CardView cvCompetitions;
		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			cvCompetitions.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			listItemClickListener.onListItemClick(getAdapterPosition());
		}
	}

	public interface ListItemClickListener {
		void onListItemClick(int clickedItemIndex);
	}
}
