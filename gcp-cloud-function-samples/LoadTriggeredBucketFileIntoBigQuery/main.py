
#    This sample is that if your csv file need to transform with pandas. 

from google.cloud import bigquery
import pandas as pd
def csv_in_gcs_to_table(event, context):    

    client = bigquery.Client()

    bucket_name = "BUCKET_NAME"
    object_name = event['name'] 
    table_id = "GCP_PROJECT_NAME.DATASET_NAME.TABLE_NAME"

    job_config = bigquery.LoadJobConfig(schema = [
        bigquery.SchemaField('Column1','STRING'),       
        bigquery.SchemaField('Column2','INTEGER'),
        bigquery.SchemaField('Column3','DATETIME'),
        bigquery.SchemaField('Column4','DATETIME'),
        bigquery.SchemaField('Column5','FLOAT64'),
    ])

    uri = f"gs://{bucket_name}/{object_name}"

    df = pd.read_csv(uri, sep = '|')
    df['Column1'] = df['Column1'].astype(str)
    df['Column3'] = pd.to_datetime(df.Column3)
    df['Column4'] = pd.to_datetime(df.Column4, format='%Y-%m-%d %H:%M:%S')    

    load_job = client.load_table_from_dataframe(df,
                                          table_id,
                                          job_config=job_config)
    load_job.result() 