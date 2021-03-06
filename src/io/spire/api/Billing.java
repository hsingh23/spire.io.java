/**
 * 
 */
package io.spire.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.spire.api.Api.ApiDescriptionModel.ApiSchemaModel;

/**
 * Spire Billing plans
 * 
 * @since 1.0
 * @author Jorge Gonzalez
 * @deprecated v1.1.4
 *
 */
public class Billing extends Resource {

	private List<Plan> plans;
	
	/**
	 * 
	 */
	public Billing() {
		super();
	}

	/**
	 * 
	 * @param schema
	 */
	public Billing(ApiSchemaModel schema) {
		super(schema);
	}

	/**
	 * 
	 * @param model
	 * @param schema
	 */
	public Billing(ResourceModel model, ApiSchemaModel schema) {
		super(model, schema);
	}

	/* (non-Javadoc)
	 * @see io.spire.api.Resource#initialize()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize() {
		super.initialize();
		plans = new ArrayList<Plan>();
		List<Map<String, Object>> rawPlans = model.getProperty("plans", List.class);
		for (Map<String, Object> rawPlan : rawPlans) {
			ResourceModel rawModelPlan = new ResourceModel(rawPlan);
			Plan p = new Plan(rawModelPlan, this.schema);
			plans.add(p);
		}
	}

	/* (non-Javadoc)
	 * @see io.spire.api.Resource#getResourceName()
	 */
	@Override
	public String getResourceName() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	@Override
	protected void addModel(Map<String, Object> rawModel) {
		
	}
	
	/**
	 * Gets a list of {@link Plan}
	 * 
	 * @return List<Plans>
	 */
	public List<Plan> getPlans(){
		return plans;
	}
	
	/**
	 * Describes features of a {@link BillingSubscription}
	 * 
	 * @since 1.0
	 * @author Jorge Gonzalez
	 *
	 */
	public static class Plan extends Resource{
		private Features features;
		
		/**
		 * 
		 */
		public Plan() {
			super();
		}

		/**
		 * 
		 * @param schema
		 */
		public Plan(ApiSchemaModel schema) {
			super(schema);
		}

		/**
		 * 
		 * @param model
		 * @param schema
		 */
		public Plan(ResourceModel model, ApiSchemaModel schema) {
			super(model, schema);
		}
		
		/**
		 * 
		 * @return String
		 */
		public String getId(){
			return this.model.getProperty("id", String.class);
		}
		
		@Override
		public String getName(){
			return this.model.getProperty("name", String.class);
		}
		
		/**
		 * 
		 * @return String
		 */
		public String getDescription(){
			return this.model.getProperty("description", String.class);
		}
		
		/**
		 * 
		 * @return Double
		 */
		public Double getPrice(){
			return Double.parseDouble(this.model.getProperty("price", String.class));
		}
		
		/**
		 * Gets the Plan features
		 * 
		 * @return Features
		 */
		public Features getFeatures(){
			return features;
		}
		
		@Override
		protected void initialize() {
			super.initialize();
			ResourceModel featuresModel = getResourceModel("features");
			features = new Features(featuresModel, schema);
		}

		@Override
		public String getResourceName() {
			return this.getClass().getSimpleName().toLowerCase();
		}
		
		@Override
		protected void addModel(Map<String, Object> rawModel) {
			
		}
		
		/**
		 * Billing plan features
		 * 
		 * @since 1.0
		 * @author Jorge Gonzalez
		 *
		 */
		public static class Features extends Resource{
			private Queue queue;
			
			/**
			 * 
			 */
			public Features() {
				super();
			}

			/**
			 * 
			 * @param schema
			 */
			public Features(ApiSchemaModel schema) {
				super(schema);
			}

			/**
			 * 
			 * @param model
			 * @param schema
			 */
			public Features(ResourceModel model, ApiSchemaModel schema) {
				super(model, schema);
			}
			
			@Override
			protected void initialize() {
				super.initialize();
				ResourceModel queueModel = getResourceModel("queue");
				queue = new Queue(queueModel, schema);
			}

			@Override
			public String getResourceName() {
				return this.getClass().getSimpleName().toLowerCase();
			}
			
			@Override
			protected void addModel(Map<String, Object> rawModel) {
				
			}
			
			/**
			 * Gets the requests per second (rps)
			 * 
			 * @return Integer
			 */
			public Integer getRPS(){
				return this.model.getProperty("rps", Integer.class);
			}
			
			/**
			 * 
			 * @return Queue
			 */
			public Queue getQueue(){
				return queue;
			}
			
			/**
			 * Describes technical features of the service
			 * 
			 * @since 1.0
			 * @author Jorge Gonzalez
			 *
			 */
			public static class Queue extends Resource{
				/**
				 * 
				 */
				public Queue() {
					super();
				}

				/**
				 * 
				 * @param schema
				 */
				public Queue(ApiSchemaModel schema) {
					super(schema);
				}

				/**
				 * 
				 * @param model
				 * @param schema
				 */
				public Queue(ResourceModel model, ApiSchemaModel schema) {
					super(model, schema);
				}
				
				/**
				 * 
				 * @return Integer
				 */
				public Integer getLimit(){
					return this.model.getProperty("limit", Integer.class);
				}

				@Override
				public String getResourceName() {
					return this.getClass().getSimpleName().toLowerCase();
				}
				
				@Override
				protected void addModel(Map<String, Object> rawModel) {
					
				}
			}
		}
	}
}
