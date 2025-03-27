CREATE TABLE public.promotion (
	id int8 NOT NULL GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE),
	user_id int8 NOT NULL,
	status int2 NOT NULL DEFAULT 0,
	total_views int4 NOT NULL DEFAULT 0,
	remaining_views int4 NULL DEFAULT 0,
	monetary_asset numeric(19, 4) NOT NULL DEFAULT 0,
	created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,	
	CONSTRAINT promotion_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_promotion_id ON public.promotion (id);
CREATE INDEX idx_promotion_user_id ON public.promotion (user_id);
CREATE INDEX idx_promotion_end_date ON public.promotion (end_date);
CREATE INDEX idx_promotion_status ON public.promotion (status);

-- Композитные индексы для частых запросов
CREATE INDEX idx_promotion_user_status ON public.promotion (user_id, status);
CREATE INDEX idx_promotion_date_range ON public.promotion (start_date, end_date);